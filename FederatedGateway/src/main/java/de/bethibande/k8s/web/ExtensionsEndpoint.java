package de.bethibande.k8s.web;

import de.bethibande.k8s.resources.EndpointCapability;
import de.bethibande.k8s.resources.MonitorFederatedExtension;
import de.bethibande.k8s.routing.FederatedRouter;
import de.bethibande.k8s.watcher.ExtensionDiscoveryService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import java.util.List;

@Path("/api/v1/extensions")
public class ExtensionsEndpoint {

    public static final String FEDERATED_MODULE_URL_TEMPLATE = "/fed/%s/web/assets/remoteEntry.js";

    private final ExtensionDiscoveryService extensionDiscoveryService;
    private final FederatedRouter federatedRouter;

    @Inject
    public ExtensionsEndpoint(final ExtensionDiscoveryService extensionDiscoveryService, final FederatedRouter federatedRouter) {
        this.extensionDiscoveryService = extensionDiscoveryService;
        this.federatedRouter = federatedRouter;
    }

    @GET
    public List<MonitorFederatedExtension> getExtensions() {
        return extensionDiscoveryService.list();
    }

    @GET
    @Path("/web-modules")
    public List<String> getWebModuleEndpoints() {
        return federatedRouter.getAllRoutesByCapability(EndpointCapability.WEB_MODULE)
                .stream()
                .map(route -> FEDERATED_MODULE_URL_TEMPLATE.formatted(route.extension().getMetadata().getName()))
                .toList();
    }

}
