package de.bethibande.k8s;

import java.util.Collection;

/**
 * Represents a listener registration. Use {@link Registration#remove()} to unregister your listener.
 * For easy instantiation use {@link Registration#addAndReturn(Object, Collection)}.
 */
public interface Registration {

    static <T> Registration addAndReturn(final T value, final Collection<T> listeners) {
        listeners.add(value);
        return () -> listeners.remove(value);
    }

    void remove();

}
