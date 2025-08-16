package de.bethibande.k8s.routing;

import de.bethibande.k8s.resources.MonitorFederatedEndpoint;
import de.bethibande.k8s.resources.MonitorFederatedExtension;

import java.util.regex.Pattern;

public record FederatedRoute(
        Pattern pattern,
        MonitorFederatedEndpoint endpoint,
        MonitorFederatedExtension extension
) {
}
