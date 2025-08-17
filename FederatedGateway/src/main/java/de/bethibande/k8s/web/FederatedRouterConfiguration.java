package de.bethibande.k8s.web;

import de.bethibande.k8s.routing.FederatedRouter;
import io.quarkus.arc.All;
import io.quarkus.runtime.Startup;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@Startup
@ApplicationScoped
public class FederatedRouterConfiguration {

    private final Router router;
    private final Vertx vertx;
    private final FederatedRouter federatedRouter;
    private final List<FederatedFileRewrite> fileRewrites;

    public FederatedRouterConfiguration(final Router router,
                                        final Vertx vertx,
                                        final FederatedRouter federatedRouter,
                                        final @All List<FederatedFileRewrite> fileRewrites) {
        this.router = router;
        this.vertx = vertx;
        this.federatedRouter = federatedRouter;
        this.fileRewrites = fileRewrites;
    }

    @PostConstruct
    void init() {
        router.route("/fed/*").handler(new FederationProxy(vertx, federatedRouter, fileRewrites));
    }

}
