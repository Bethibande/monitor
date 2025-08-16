package de.bethibande.k8s.routing;

import de.bethibande.k8s.resources.EndpointCapability;
import de.bethibande.k8s.resources.MonitorFederatedEndpoint;
import de.bethibande.k8s.resources.MonitorFederatedExtension;
import de.bethibande.k8s.watcher.ExtensionDiscoveryService;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Startup
@ApplicationScoped
public class FederatedRouter {

    private final Map<String, List<FederatedRoute>> routes = new HashMap<>();
    private final ExtensionDiscoveryService extensionDiscoveryService;

    @Inject
    public FederatedRouter(final ExtensionDiscoveryService extensionDiscoveryService) {
        this.extensionDiscoveryService = extensionDiscoveryService;
    }

    @PostConstruct
    void init() {
        for (final MonitorFederatedExtension monitorFederatedExtension : extensionDiscoveryService.list()) {
            addOrUpdateRoutes(monitorFederatedExtension);
        }

        extensionDiscoveryService.onChange(((action, extension) -> {
            switch (action) {
                case ADDED, MODIFIED -> addOrUpdateRoutes(extension);
                case DELETED -> removeRoutes(extension);
                default -> {
                }
            }
        }));
    }

    private Optional<FederatedRoute> matchRoute(final List<FederatedRoute> routes, final String path) {
        for (final FederatedRoute route : routes) {
            final Matcher matcher = route.pattern().matcher(path);
            if (matcher.matches()) {
                return Optional.of(route);
            }
        }

        return Optional.empty();
    }

    public Optional<FederatedRoute> matchRoute(final String extensionId, final String subpath) {
        return Optional.ofNullable(routes.get(extensionId)).flatMap(routes -> matchRoute(routes, subpath));
    }

    private String capabilityToPathPrefix(final EndpointCapability capability) {
        return switch (capability) {
            case TRANSLATIONS -> "/translations";
            case WEB_MODULE -> "/web";
            case API -> "/api";
        };
    }

    private Pattern capabilityToPathPattern(final EndpointCapability capability) {
        return switch (capability) {
            case TRANSLATIONS -> Pattern.compile("^/translations");
            case WEB_MODULE -> Pattern.compile("^/web/assets/module.js$");
            case API -> Pattern.compile("/api(?<path>/.*)?");
        };
    }

    private FederatedRoute toRoute(final MonitorFederatedEndpoint endpoint,
                                   final MonitorFederatedExtension extension) {
        return new FederatedRoute(
                capabilityToPathPrefix(endpoint.getCapability()),
                capabilityToPathPattern(endpoint.getCapability()),
                endpoint,
                extension
        );
    }

    public void removeRoutes(final MonitorFederatedExtension extension) {
        this.routes.remove(extension.getMetadata().getName());
    }

    public void addOrUpdateRoutes(final MonitorFederatedExtension extension) {
        final List<FederatedRoute> routes = new ArrayList<>();
        for (final MonitorFederatedEndpoint endpoint : extension.getSpec().getEndpoints()) {
            routes.add(this.toRoute(endpoint, extension));
        }

        this.routes.put(extension.getMetadata().getName(), routes);
    }

}
