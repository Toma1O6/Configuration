package dev.toma.configuration.config.validate;

import dev.toma.configuration.config.value.INumericValue;

import java.util.function.Predicate;

public final class NumberRange<T extends Number & Comparable<T>> implements Predicate<T> {

    private final T min;
    private final T max;

    private NumberRange(T min, T max, INumericValue<T> value) {
        this.min = value.min().compareTo(min) < 0 ? min : value.min();
        this.max = value.max().compareTo(max) > 0 ? max : value.max();
    }

    public static <N extends Number & Comparable<N>> NumberRange<N> all(INumericValue<N> config) {
        return new NumberRange<>(config.min(), config.max(), config);
    }

    public static <N extends Number & Comparable<N>> NumberRange<N> interval(INumericValue<N> config, N min, N max) {
        return new NumberRange<>(min, max, config);
    }

    public T min() {
        return this.min;
    }

    public T max() {
        return this.max;
    }

    @Override
    public boolean test(T t) {
        return this.isWithinRange(t);
    }

    public boolean isWithinRange(T t) {
        int minBoundCompare = t.compareTo(this.min());
        if (minBoundCompare < 0) {
            return false;
        }
        int maxBoundCompare = t.compareTo(this.max());
        return maxBoundCompare <= 0;
    }

    public T clamp(T t) {
        int minBound = t.compareTo(this.min());
        if (minBound < 0) {
            return min();
        }
        int maxBound = t.compareTo(this.max());
        return maxBound > 0 ? max() : t;
    }
}
