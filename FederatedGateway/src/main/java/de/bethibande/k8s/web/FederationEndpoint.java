package de.bethibande.k8s.web;

import de.bethibande.k8s.routing.FederatedRoute;
import de.bethibande.k8s.routing.FederatedRouter;
import io.vertx.core.http.HttpServerRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;

// TODO: Proxy Websocket requests
@Path("/fed/{extension-id}/{subpath:.*}")
public class FederationEndpoint {

    private static final Set<String> DISALLOWED_HEADERS = Set.of("connection", "content-length", "expect", "host", "upgrade");

    private final FederatedRouter federatedRouter;

    private final HttpClient httpClient = HttpClient.newBuilder().build();

    @Inject
    public FederationEndpoint(final FederatedRouter federatedRouter) {
        this.federatedRouter = federatedRouter;
    }

    private String buildTargetUrl(final FederatedRoute route, final String path, final HttpServerRequest request) {
        return switch (route.endpoint().getCapability()) {
            case WEB_MODULE, TRANSLATIONS -> route.endpoint().getEndpoint();
            case API -> {
                final Matcher matcher = route.pattern().matcher(path);
                final String subPath = matcher.matches() ? matcher.group("path") : "/";

                yield route.endpoint().getEndpoint() + subPath;
            }
        };
    }

    private void transferHeaders(final HttpServerRequest request, final HttpRequest.Builder builder) {
        request.headers().forEach(header -> {
            if (!DISALLOWED_HEADERS.contains(header.getKey().toLowerCase())) {
                builder.header(header.getKey(), header.getValue());
            }
        });
    }

    private HttpRequest buildRequest(final FederatedRoute route,
                                     final String path,
                                     final HttpServerRequest request) {
        final HttpRequest.Builder builder = HttpRequest.newBuilder();
        builder.uri(URI.create(buildTargetUrl(route, path, request)));
        request.headers().forEach(builder::header);

        builder.method(request.method().name(), HttpRequest.BodyPublishers.fromPublisher((e) -> {
            request.handler(buf -> {
                final byte[] bytes = buf.getBytes();
                e.onNext(ByteBuffer.wrap(bytes));
            });
            request.endHandler(v -> e.onComplete());
            request.exceptionHandler(e::onError);
        }));

        return builder.build();
    }

    private CompletableFuture<Response> sendRequest(final HttpRequest request) {
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                .thenApply(response -> {
                    final Response.ResponseBuilder builder = Response.status(response.statusCode());
                    response.headers().map().forEach((key, values) -> {
                        for (final String value : values) {
                            builder.header(key, value);
                        }
                    });

                    builder.entity(response.body());

                    return builder.build();
                });
    }

    public CompletableFuture<Response> proxy(final String extensionId,
                                             final String subpath,
                                             final HttpServerRequest request) {
        // TODO: Strip auth cookies from request to ensure we don't expose user sessions to extensions

        final Optional<FederatedRoute> optRoute = federatedRouter.matchRoute(extensionId, "/" + subpath);
        if (optRoute.isPresent()) {
            // TODO: Implement access filters
            final FederatedRoute route = optRoute.get();
            final HttpRequest requestToSend = buildRequest(route, subpath, request);
            return sendRequest(requestToSend);
        } else {
            return CompletableFuture.completedFuture(Response.status(Response.Status.NOT_FOUND).build());
        }
    }

    // TODO: Find a solution for the below mess, it seems the default quarkus rest extension doesn't provide a way to match any request method using a single method.

    @GET
    public CompletableFuture<Response> proxyGet(final @PathParam("extension-id") String extensionId,
                                                final @PathParam("subpath") String subpath,
                                                final @Context HttpServerRequest request) {
        return proxy(extensionId, subpath, request);
    }

    @POST
    public CompletableFuture<Response> proxyPost(final @PathParam("extension-id") String extensionId,
                                                 final @PathParam("subpath") String subpath,
                                                 final @Context HttpServerRequest request) {
        return proxy(extensionId, subpath, request);
    }

    @PATCH
    public CompletableFuture<Response> proxyPatch(final @PathParam("extension-id") String extensionId,
                                                  final @PathParam("subpath") String subpath,
                                                  final @Context HttpServerRequest request) {
        return proxy(extensionId, subpath, request);
    }

    @DELETE
    public CompletableFuture<Response> proxyDelete(final @PathParam("extension-id") String extensionId,
                                                   final @PathParam("subpath") String subpath,
                                                   final @Context HttpServerRequest request) {
        return proxy(extensionId, subpath, request);
    }

    @OPTIONS
    public CompletableFuture<Response> proxyOptions(final @PathParam("extension-id") String extensionId,
                                                    final @PathParam("subpath") String subpath,
                                                    final @Context HttpServerRequest request) {
        return proxy(extensionId, subpath, request);
    }

    @PUT
    public CompletableFuture<Response> proxyPut(final @PathParam("extension-id") String extensionId,
                                                final @PathParam("subpath") String subpath,
                                                final @Context HttpServerRequest request) {
        return proxy(extensionId, subpath, request);
    }

    @HEAD
    public CompletableFuture<Response> proxyHead(final @PathParam("extension-id") String extensionId,
                                                 final @PathParam("subpath") String subpath,
                                                 final @Context HttpServerRequest request) {
        return proxy(extensionId, subpath, request);
    }

}
