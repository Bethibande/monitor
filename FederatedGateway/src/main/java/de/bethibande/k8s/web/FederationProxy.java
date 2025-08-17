package de.bethibande.k8s.web;

import de.bethibande.k8s.routing.FederatedRoute;
import de.bethibande.k8s.routing.FederatedRouter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;

public class FederationProxy implements Handler<RoutingContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FederationProxy.class);

    private final Map<String, FederatedFileRewrite> fileRewrites = new HashMap<>();

    private final FederatedRouter router;
    private final ClassLoader currentClassLoader;
    private final FederationWebSocketProxy websocketProxy;
    private final WebClient client;

    public FederationProxy(final Vertx vertx,
                           final FederatedRouter router,
                           final List<FederatedFileRewrite> fileRewrites) {
        this.currentClassLoader = Thread.currentThread().getContextClassLoader();
        this.client = WebClient.create(vertx);
        this.websocketProxy = new FederationWebSocketProxy(vertx, router);
        this.router = router;
        for (final FederatedFileRewrite fileReWriter : fileRewrites) {
            this.fileRewrites.put(fileReWriter.getResourcePath(), fileReWriter);
        }
    }

    @Override
    public void handle(final RoutingContext ctx) {
        final String normalizedPath = ctx.normalizedPath();
        if (!RouterUtils.isFederatedEndpoint(normalizedPath)) {
            RouterUtils.next(currentClassLoader, ctx);
            return;
        }

        if (isWebSocketUpdate(ctx)) {
            websocketProxy.handle(ctx);
        } else {
            handleHttpRequest(ctx, normalizedPath);
        }
    }

    private void handleHttpRequest(final RoutingContext ctx, final String normalizedPath) {
        final Matcher matcher = RouterUtils.ROUTE_PATTERN.matcher(normalizedPath);
        final String extensionId = matcher.matches() ? matcher.group("extensionId") : null;
        final String subPath = matcher.matches() ? matcher.group("subpath") : null;
        if (extensionId == null || subPath == null) {
            ctx.fail(404);
            return;
        }

        final Optional<FederatedRoute> optRoute = this.router.matchRoute(extensionId, subPath);
        if (optRoute.isPresent()) {
            final FederatedRoute route = optRoute.get();
            final HttpServerRequest request = ctx.request();
            final MultiMap headers = request.headers();

            final URI uri = URI.create(RouterUtils.buildTargetUrl(route, subPath));
            final String host = uri.getHost();
            final int port = extractPort(uri);

            client.request(request.method(), port, host, uri.toString())
                    .putHeaders(headers)
                    .send(event -> {
                        if (event.succeeded()) {
                            final int statusCode = event.result().statusCode();
                            switch (statusCode) {
                                case 200:
                                    forwardResponse(event, ctx, subPath, extensionId);
                                    break;
                                case 404:
                                    ctx.fail(404);
                                    break;
                                default:
                                    forwardError(event, statusCode, ctx);
                            }
                        } else {
                            error(event, ctx);
                        }
                    });
        } else {
            ctx.fail(404);
        }
    }

    private int extractPort(final URI uri) {
        if (uri.getPort() > 0) return uri.getPort();
        return uri.getScheme().equalsIgnoreCase("https") ? 443 : 80;
    }

    private void forwardError(final AsyncResult<HttpResponse<Buffer>> event,
                              final int statusCode,
                              final RoutingContext ctx) {
        final Buffer body = event.result().body();
        final HttpServerResponse response = ctx.response().setStatusCode(statusCode);
        if (body != null) {
            response.send(body);
        } else {
            response.send();
        }
    }

    private void forwardResponse(final AsyncResult<HttpResponse<Buffer>> event,
                                 final RoutingContext ctx,
                                 final String resourcePath,
                                 final String extensionId) {
        final HttpServerResponse response = ctx.response();
        response.headers().setAll(event.result().headers());
        final Buffer body = event.result().body();
        if (body != null) {
            if (this.fileRewrites.containsKey(resourcePath)) {
                final String content = body.toString(StandardCharsets.UTF_8);
                final String rewritten = this.fileRewrites.get(resourcePath).rewrite(content, extensionId);

                response.putHeader("Content-Length", String.valueOf(rewritten.length()));
                response.write(Buffer.buffer(rewritten));
                response.end();
                return;
            }
            response.send(body);
        } else {
            response.send();
        }

    }

    private boolean isWebSocketUpdate(final RoutingContext ctx) {
        return ctx.request().headers().contains("Upgrade")
                && "websocket".equalsIgnoreCase(ctx.request().headers().get("Upgrade"));
    }

    private void error(final AsyncResult<HttpResponse<Buffer>> event, final RoutingContext ctx) {
        final String error = String.format("Router failed to forward request '%s', see logs.", ctx.request().uri());
        ctx.response().setStatusCode(500);
        ctx.response().send(error);
        LOGGER.error(error, event.cause());
    }
}
