package de.bethibande.k8s.web;

import de.bethibande.k8s.routing.FederatedRoute;
import de.bethibande.k8s.routing.FederatedRouter;
import io.netty.util.internal.StringUtil;
import io.vertx.core.Vertx;
import io.vertx.core.http.*;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;

public class FederationWebSocketProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(FederationWebSocketProxy.class);

    private final WebSocketClient client;
    private final FederatedRouter router;

    public FederationWebSocketProxy(final Vertx vertx, final FederatedRouter router) {
        this.client = vertx.createWebSocketClient();
        this.router = router;
    }

    public void handle(final RoutingContext ctx) {
        final HttpServerRequest request = ctx.request();
        ctx.request().pause();

        final String uri = request.uri();
        final Matcher matcher = RouterUtils.ROUTE_PATTERN.matcher(uri);
        final String extensionId = matcher.matches() ? matcher.group("extensionId") : null;
        final String subPath = matcher.matches() ? matcher.group("subpath") : null;
        if (extensionId == null || subPath == null) {
            ctx.fail(404);
            return;
        }

        final Optional<FederatedRoute> optRoute = this.router.matchRoute(extensionId, subPath);
        if (optRoute.isEmpty()) {
            ctx.fail(404);
            return;
        }

        final FederatedRoute route = optRoute.get();
        final String forwardUri = RouterUtils.buildTargetUrl(route, subPath);

        request.toWebSocket(r -> {
            if (r.succeeded()) {
                final ServerWebSocket serverWebSocket = r.result();
                final AtomicReference<WebSocket> clientWebSocket = new AtomicReference<>();

                serverWebSocket.exceptionHandler(e -> LOGGER.error("An error occurred while handling websocket connection", e))
                        .closeHandler(v -> {
                            clientWebSocket.getAndUpdate(socket -> {
                                if (socket != null && !socket.isClosed()) {
                                    socket.close();
                                }
                                return null;
                            });
                        });

                final String subProtocol = serverWebSocket.subProtocol();
                final List<String> subProtocols = new ArrayList<>(1);
                if (!StringUtil.isNullOrEmpty(subProtocol)) {
                    subProtocols.add(subProtocol);
                    ;
                }

                final WebSocketConnectOptions options = new WebSocketConnectOptions()
                        .setURI(forwardUri)
                        .setHeaders(serverWebSocket.headers())
                        .setSubProtocols(subProtocols)
                        .setAllowOriginHeader(false);

                serverWebSocket.accept();

                this.client.connect(options, clientContext -> {
                    if (clientContext.succeeded()) {
                        clientWebSocket.set(clientContext.result());
                        // messages from Node.js forwarded back to browser
                        clientWebSocket.get()
                                .exceptionHandler((e) -> LOGGER.error("WebSocket Client closed with error: {}", e.getMessage(), e))
                                .closeHandler((v) -> serverWebSocket.close()).textMessageHandler(serverWebSocket::writeTextMessage);

                        serverWebSocket.textMessageHandler((msg) -> {
                            final WebSocket w = clientWebSocket.get();
                            if (w != null && !w.isClosed()) {
                                w.writeTextMessage(msg);
                            }
                        });
                    } else {
                        LOGGER.error("WebSocket connection failed", clientContext.cause());
                    }
                });
            } else {
                LOGGER.error("Failed to upgrade to websocket connection", r.cause());
            }
        });
    }

}
