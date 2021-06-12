package dev.toma.configuration.api;

public interface IBounded<N extends Number> {

    boolean isWithinBounds(N input);

    N getMin();

    N getMax();
}
