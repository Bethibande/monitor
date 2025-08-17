package de.bethibande.k8s.routing;

import de.bethibande.k8s.resources.EndpointCapability;
import de.bethibande.k8s.resources.MonitorFederatedEndpoint;
import de.bethibande.k8s.resources.MonitorFederatedExtension;
import de.bethibande.k8s.watcher.ExtensionDiscoveryService;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Startup
@ApplicationScoped
public class FederatedRouter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FederatedRouter.class);

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
            LOGGER.info("Router updated");
        }));
    }

    private Optional<FederatedRoute> matchRoute(final List<FederatedRoute> routes, final String path) {
        final String normalizedPath = path.startsWith("/") ? path : "/" + path;
        for (final FederatedRoute route : routes) {
            final Matcher matcher = route.pattern().matcher(normalizedPath);
            if (matcher.matches()) {
                return Optional.of(route);
            }
        }

        return Optional.empty();
    }

    public Optional<FederatedRoute> matchRoute(final String extensionId, final String subpath) {
        return Optional.ofNullable(routes.get(extensionId))
                .flatMap(routes -> matchRoute(routes, subpath));
    }

    private Pattern capabilityToPathPattern(final EndpointCapability capability) {
        return switch (capability) {
            case TRANSLATIONS -> Pattern.compile("^/translations");
            case WEB_MODULE -> Pattern.compile("^/web(?<path>/.*)?");
            case API -> Pattern.compile("^/api(?<path>/.*)?");
        };
    }

    private FederatedRoute toRoute(final MonitorFederatedEndpoint endpoint,
                                   final MonitorFederatedExtension extension) {
        return new FederatedRoute(
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

    public List<FederatedRoute> getAllRoutesByCapability(final EndpointCapability capability) {
        return this.routes.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(endpoint -> endpoint.endpoint().getCapability() == capability)
                .toList();
    }

}
