package de.bethibande.k8s.resources;

import de.bethibande.k8s.resources.MonitorFederatedEndpoint;
import io.fabric8.generator.annotation.Required;
import io.fabric8.generator.annotation.Size;

import java.util.List;

public class MonitorFederatedExtensionSpec {

    @Required
    @Size(min = 1)
    private List<MonitorFederatedEndpoint> endpoints;

    public void setEndpoints(final List<MonitorFederatedEndpoint> endpoints) {
        this.endpoints = endpoints;
    }

    public List<MonitorFederatedEndpoint> getEndpoints() {
        return endpoints;
    }
}
