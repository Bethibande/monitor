package de.bethibande.k8s.web;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.client.KubernetesClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import java.util.ArrayList;
import java.util.List;

@Path("/api/v1/context")
public class ContextEndpoint {

    private final KubernetesClient kubernetesClient;

    @Inject
    public ContextEndpoint(final KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    public enum ContextScope {
        CLUSTER,
        NODE,
        NAMESPACE
    }

    public record ClusterContext(
            String id,
            ContextScope scope,
            ClusterContext parent
    ) {
    }

    @GET
    public List<ClusterContext> list() {
        final ClusterContext cluster = new ClusterContext(
                "host",
                ContextScope.CLUSTER,
                null
        );

        final List<ClusterContext> clusterContexts = new ArrayList<>();
        clusterContexts.add(cluster);

        kubernetesClient.resources(Node.class)
                .list()
                .getItems()
                .stream()
                .map(node -> new ClusterContext(
                        node.getMetadata().getName(),
                        ContextScope.NODE,
                        cluster
                )).forEach(clusterContexts::add);

        kubernetesClient.resources(Namespace.class)
                .list()
                .getItems()
                .stream()
                .map(namespace -> new ClusterContext(
                        namespace.getMetadata().getName(),
                        ContextScope.NAMESPACE,
                        cluster
                )).forEach(clusterContexts::add);


        return clusterContexts;
    }

}
