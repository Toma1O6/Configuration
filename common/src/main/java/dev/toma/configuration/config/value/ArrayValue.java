package dev.toma.configuration.config.value;

public interface ArrayValue<T> extends HierarchicalConfigValue {

    boolean isFixedSize();

    T createElementInstance();
}
