package dev.toma.configuration.internal;

public interface Ranged<N extends Number> {

    boolean isInRange(N input);

    N getMin();

    N getMax();
}
