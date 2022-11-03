package dev.toma.configuration.config.value;

public interface ArrayValue {

    boolean isFixedSize();

    default String elementToString(Object element) {
        return element.toString();
    }
}
