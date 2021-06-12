package dev.toma.configuration.api;

import java.util.function.Function;

public class NameableWrapper<T> implements INameable {

    private final Function<T, String> keyExtractor;
    private final Function<String, String> formatter;
    private final T wrappedElement;

    private NameableWrapper(T wrappedElement, Function<T, String> keyExtractor, Function<String, String> formatter) {
        this.keyExtractor = keyExtractor;
        this.formatter = formatter;
        this.wrappedElement = wrappedElement;
    }

    public T getElement() {
        return wrappedElement;
    }

    @Override
    public String getUnformattedName() {
        return keyExtractor.apply(wrappedElement);
    }

    @Override
    public String getFormattedName() {
        String unformattedName = getUnformattedName();
        return formatter.apply(unformattedName);
    }

    @SuppressWarnings("unchecked")
    public static <T> NameableWrapper<T>[] wrap(T[] elements, Function<T, String> keyExtractor, Function<String, String> formatter) {
        NameableWrapper<T>[] array = (NameableWrapper<T>[]) new NameableWrapper[elements.length];
        int j = 0;
        for (T t : elements) {
            array[j++] = new NameableWrapper<>(t, keyExtractor, formatter);
        }
        return array;
    }

    public static <T> NameableWrapper<T>[] wrap(T[] elements, Function<T, String> keyExtractor) {
        return wrap(elements, keyExtractor, Function.identity());
    }
}
