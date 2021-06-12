package dev.toma.configuration.api;

/**
 * Represents bounds for numbers
 * @param <N> Number type
 */
public interface IBounded<N extends Number> {

    /**
     * Check whether input value is within defined bounds
     * @param input Input to compare
     * @return Range check result
     */
    boolean isWithinBounds(N input);

    /**
     * @return Lowest valid bound of this type
     */
    N getMin();

    /**
     * @return Highest valid bound of this type
     */
    N getMax();
}
