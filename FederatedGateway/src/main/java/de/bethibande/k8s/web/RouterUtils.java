package de.bethibande.k8s.web;

import de.bethibande.k8s.routing.FederatedRoute;
import io.vertx.ext.web.RoutingContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouterUtils {

    public static final Pattern ROUTE_PATTERN = Pattern.compile("^/fed/(?<extensionId>[^/]+)/(?<subpath>.*)$");

    public static String buildTargetUrl(final FederatedRoute route, final String path) {
        final String normalizedPath = path.startsWith("/") ? path : "/" + path;

        return switch (route.endpoint().getCapability()) {
            case API, WEB_MODULE -> {
                final Matcher matcher = route.pattern().matcher(normalizedPath);
                final String subPath = matcher.matches() ? matcher.group("path") : "/";

                yield route.endpoint().getEndpoint() + subPath;
            }
        };
    }

    public static boolean isFederatedEndpoint(final String normalizedPath) {
        return normalizedPath.startsWith("/fed/");
    }

    public static void next(final ClassLoader cl, final RoutingContext ctx) {
        Thread.currentThread().setContextClassLoader(cl);
        ctx.next();
    }

}
