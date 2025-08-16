package de.bethibande.k8s.web;

import de.bethibande.k8s.resources.MonitorFederatedExtension;
import de.bethibande.k8s.watcher.ExtensionDiscoveryService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import java.util.List;

@Path("/api/v1/extensions")
public class ExtensionsEndpoint {

    private final ExtensionDiscoveryService extensionDiscoveryService;

    @Inject
    public ExtensionsEndpoint(final ExtensionDiscoveryService extensionDiscoveryService) {
        this.extensionDiscoveryService = extensionDiscoveryService;
    }

    @GET
    public List<MonitorFederatedExtension> getExtensions() {
        return extensionDiscoveryService.list();
    }

    @GET
    @Path("/web-modules")
    public List<String> getWebModuleEndpoints() {
        return List.of();
    }

}
