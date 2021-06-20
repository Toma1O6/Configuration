package dev.toma.configuration.util;

import java.util.function.Consumer;

public interface IListener<E> {

    void invoke(E arg);

    void listen(Consumer<E> event);

    void stopListening(Consumer<E> event);
}
