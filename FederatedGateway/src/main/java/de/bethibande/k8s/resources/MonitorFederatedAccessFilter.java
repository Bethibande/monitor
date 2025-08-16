package de.bethibande.k8s.resources;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.fabric8.generator.annotation.Required;

public class MonitorFederatedAccessFilter {

    @Required
    @JsonPropertyDescription("This property specifies the path that'll be matched. Any request starting with this path will be matched by this filter")
    private String path;

    @Required
    @JsonPropertyDescription("The permission required to access the matched route")
    private String permission;

    public void setPath(final String path) {
        this.path = path;
    }

    public void setPermission(final String permission) {
        this.permission = permission;
    }

    public String getPath() {
        return path;
    }

    public String getPermission() {
        return permission;
    }
}
