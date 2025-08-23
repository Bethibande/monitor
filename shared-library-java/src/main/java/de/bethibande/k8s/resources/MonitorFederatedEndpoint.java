package de.bethibande.k8s.resources;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import de.bethibande.k8s.resources.EndpointCapability;
import de.bethibande.k8s.resources.MonitorFederatedAccessFilter;
import io.fabric8.generator.annotation.Required;
import io.fabric8.generator.annotation.Size;

import java.util.List;

public class MonitorFederatedEndpoint {

    @Required
    @JsonPropertyDescription("The URL providing the endpoint/capability. For example: http://my.service.svc.cluster.local/api")
    private String endpoint;

    @Required
    @JsonPropertyDescription("The capability of the endpoint.")
    private EndpointCapability capability;

    @Size(min = 0)
    @JsonPropertyDescription("Filters used to restrict access to certain sub-paths based on user permissions.")
    private List<MonitorFederatedAccessFilter> filters;

    public void setEndpoint(final String endpoint) {
        this.endpoint = endpoint;
    }

    public void setCapability(final EndpointCapability capability) {
        this.capability = capability;
    }

    public void setFilters(final List<MonitorFederatedAccessFilter> filters) {
        this.filters = filters;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public EndpointCapability getCapability() {
        return capability;
    }

    public List<MonitorFederatedAccessFilter> getFilters() {
        return filters;
    }
}
