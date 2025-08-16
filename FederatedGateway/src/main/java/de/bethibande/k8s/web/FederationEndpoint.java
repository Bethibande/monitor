package de.bethibande.k8s.web;

import de.bethibande.k8s.routing.FederatedRoute;
import de.bethibande.k8s.routing.FederatedRouter;
import io.vertx.core.http.HttpServerRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

import java.util.Optional;

@Path("/fed/{extension-id}/{subpath:.*}")
public class FederationEndpoint {

    private final FederatedRouter federatedRouter;

    @Inject
    public FederationEndpoint(final FederatedRouter federatedRouter) {
        this.federatedRouter = federatedRouter;
    }

    public Response proxy(final String extensionId,
                          final String subpath,
                          final HttpServerRequest request) {
        // TODO: Strip auth cookies from request to ensure we don't expose user sessions to extensions

        final Optional<FederatedRoute> optRoute = federatedRouter.matchRoute(extensionId, "/" + subpath);
        if (optRoute.isPresent()) {
            // TODO: Implement access filters
            final FederatedRoute route = optRoute.get();
            return Response.ok(route).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // TODO: Find a solution for the below mess, it seems the default quarkus rest extension doesn't provide a way to match any request method using a single method.

    @GET
    public Response proxyGet(final @PathParam("extension-id") String extensionId,
                             final @PathParam("subpath") String subpath,
                             final @Context HttpServerRequest request) {
        return proxy(extensionId, subpath, request);
    }

    @POST
    public Response proxyPost(final @PathParam("extension-id") String extensionId,
                              final @PathParam("subpath") String subpath,
                              final @Context HttpServerRequest request) {
        return proxy(extensionId, subpath, request);
    }

    @PATCH
    public Response proxyPatch(final @PathParam("extension-id") String extensionId,
                               final @PathParam("subpath") String subpath,
                               final @Context HttpServerRequest request) {
        return proxy(extensionId, subpath, request);
    }

    @DELETE
    public Response proxyDelete(final @PathParam("extension-id") String extensionId,
                                final @PathParam("subpath") String subpath,
                                final @Context HttpServerRequest request) {
        return proxy(extensionId, subpath, request);
    }

    @OPTIONS
    public Response proxyOptions(final @PathParam("extension-id") String extensionId,
                                 final @PathParam("subpath") String subpath,
                                 final @Context HttpServerRequest request) {
        return proxy(extensionId, subpath, request);
    }

    @PUT
    public Response proxyPut(final @PathParam("extension-id") String extensionId,
                             final @PathParam("subpath") String subpath,
                             final @Context HttpServerRequest request) {
        return proxy(extensionId, subpath, request);
    }

    @HEAD
    public Response proxyHead(final @PathParam("extension-id") String extensionId,
                              final @PathParam("subpath") String subpath,
                              final @Context HttpServerRequest request) {
        return proxy(extensionId, subpath, request);
    }

}
