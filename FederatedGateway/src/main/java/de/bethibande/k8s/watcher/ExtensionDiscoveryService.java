package de.bethibande.k8s.watcher;

import de.bethibande.k8s.Registration;
import de.bethibande.k8s.resources.MonitorFederatedExtension;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BiConsumer;

@Startup
@ApplicationScoped
public class ExtensionDiscoveryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionDiscoveryService.class);

    private final KubernetesClient client;

    private final Map<String, MonitorFederatedExtension> extensions = new HashMap<>();

    private Watch watch;

    private final List<BiConsumer<Watcher.Action, MonitorFederatedExtension>> listeners = new ArrayList<>();

    public ExtensionDiscoveryService(final KubernetesClient client) {
        this.client = client;
    }

    @PreDestroy
    public void close() {
        if (this.watch != null) {
            this.watch.close();
        }
    }

    @PostConstruct
    void init() {
        final List<MonitorFederatedExtension> extensions = this.listAll();
        for (final MonitorFederatedExtension extension : extensions) {
            this.extensions.put(extension.getMetadata().getName(), extension);
        }

        LOGGER.info("Found {} extensions", this.extensions.size());

        this.watch();
    }

    private synchronized void onChange(final Watcher.Action action, final MonitorFederatedExtension resource) {
        LOGGER.info("Extension changed: {} -> {}", action, resource.getMetadata().getName());

        switch (action) {
            case ADDED, MODIFIED -> this.extensions.put(resource.getMetadata().getName(), resource);
            case DELETED -> this.extensions.remove(resource.getMetadata().getName());
            case ERROR, BOOKMARK -> {
            }
        }

        for (final BiConsumer<Watcher.Action, MonitorFederatedExtension> listener : listeners) {
            try {
                listener.accept(action, resource);
            } catch (final Throwable e) {
                LOGGER.error("Error while notifying listener", e);
            }
        }
    }

    private void onWatcherClose(final WatcherException cause) {
        watch();
    }

    private void watch() {
        final Watcher<MonitorFederatedExtension> watcher = new ExtensionWatcher(this::onChange, this::onWatcherClose);
        this.watch = client.resources(MonitorFederatedExtension.class).watch(watcher);

        LOGGER.info("Watching for extension changes");
    }

    private List<MonitorFederatedExtension> listAll() {
        return client.resources(MonitorFederatedExtension.class).list().getItems();
    }

    public List<MonitorFederatedExtension> list() {
        return new ArrayList<>(this.extensions.values());
    }

    public Optional<MonitorFederatedExtension> get(final String name) {
        return Optional.ofNullable(this.extensions.get(name));
    }

    public Registration onChange(final BiConsumer<Watcher.Action, MonitorFederatedExtension> listener) {
        return Registration.addAndReturn(listener, this.listeners);
    }

}
