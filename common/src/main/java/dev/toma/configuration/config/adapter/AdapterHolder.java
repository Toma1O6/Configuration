package dev.toma.configuration.config.adapter;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Predicate;

public record AdapterHolder<T>(TypeMatcher matcher, T adapter) implements Predicate<Class<?>>, Comparable<AdapterHolder<?>> {

    @Override
    public boolean test(Class<?> remappedType) {
        return this.matcher.test(remappedType);
    }

    @Override
    public int compareTo(@NotNull AdapterHolder<?> o) {
        return this.matcher.compareTo(o.matcher);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdapterHolder<?> that)) return false;
        return Objects.equals(matcher, that.matcher);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(matcher);
    }
}
