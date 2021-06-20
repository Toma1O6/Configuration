package dev.toma.configuration.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MultiListener<E> implements IListener<E> {

    private final List<Consumer<E>> subscribers = new ArrayList<>();

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
