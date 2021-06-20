package dev.toma.configuration.util;

public class Listeners {

    public static <E> IListener<E> multipleElementListener() {
        return new MultiListener<>();
    }
}
