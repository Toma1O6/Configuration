package dev.toma.configuration.config.value;

import dev.toma.configuration.config.validate.NumberRange;

public interface INumericValue<T extends Number & Comparable<T>> {

    T min();

    T max();

    NumberRange<T> getRange();
}
