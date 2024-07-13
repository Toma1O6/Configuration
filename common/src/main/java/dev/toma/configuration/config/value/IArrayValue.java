package dev.toma.configuration.config.value;

public interface IArrayValue<T> extends IHierarchical {

    boolean isFixedSize();

    T createElementInstance();
}
