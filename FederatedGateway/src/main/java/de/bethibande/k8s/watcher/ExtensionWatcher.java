package de.bethibande.k8s.watcher;

import de.bethibande.k8s.resources.MonitorFederatedExtension;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ExtensionWatcher implements Watcher<MonitorFederatedExtension> {

    private final BiConsumer<Action, MonitorFederatedExtension> consumer;
    private final Consumer<WatcherException> onClose;

    public ExtensionWatcher(final BiConsumer<Action, MonitorFederatedExtension> consumer,
                            final Consumer<WatcherException> onClose) {
        this.consumer = consumer;
        this.onClose = onClose;
    }

    @Override
    public boolean reconnecting() {
        return true;
    }

    @Override
    public void eventReceived(final Action action, final MonitorFederatedExtension resource) {
        this.consumer.accept(action, resource);
    }

    @Override
    public void onClose(final WatcherException cause) {
        this.onClose.accept(cause);
    }
}
