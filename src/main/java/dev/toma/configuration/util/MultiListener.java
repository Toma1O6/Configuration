package dev.toma.configuration.util;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class MultiListener<E> implements IListener<E> {

    private final Set<Consumer<E>> subscribers = new HashSet<>();

    @Override
    public void invoke(E arg) {
        subscribers.forEach(e -> e.accept(arg));
    }

    @Override
    public void listen(Consumer<E> event) {
        subscribers.add(event);
    }

    @Override
    public void stopListening(Consumer<E> event) {
        subscribers.remove(event);
    }
}
