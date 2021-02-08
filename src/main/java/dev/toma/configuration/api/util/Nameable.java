package dev.toma.configuration.api.util;

import java.util.function.Function;
import java.util.function.Supplier;

public interface Nameable {

    String getUnformattedName();

    String getFormattedName();

    class Wrapped<T> implements Nameable, Supplier<T> {

        final Function<T, String> toStringFunction;
        final Function<String, String> formattingFunction;
        final T element;

        public static <T> Wrapped<T>[] asArray(T[] input, Function<T, String> toStringFunction) {
            return asArray(input, toStringFunction, Function.identity());
        }

        public static <T> Wrapped<T>[] asArray(T[] input, Function<T, String> toStringFunction, Function<String, String> formattingFunction) {
            Wrapped<T>[] array = (Wrapped<T>[]) new Wrapped[input.length];
            for (int i = 0; i < input.length; i++) {
                T t = input[i];
                Wrapped<T> wrapped = new Wrapped<>(t, toStringFunction, formattingFunction);
                array[i] = wrapped;
            }
            return array;
        }

        public Wrapped(T element, Function<T, String> toStringFunction) {
            this(element, toStringFunction, Function.identity());
        }

        public Wrapped(T element, Function<T, String> toStringFunction, Function<String, String> formattingFunction) {
            this.element = element;
            this.toStringFunction = toStringFunction;
            this.formattingFunction = formattingFunction;
        }

        @Override
        public String getUnformattedName() {
            return toStringFunction.apply(element);
        }

        @Override
        public String getFormattedName() {
            return formattingFunction.apply(this.getUnformattedName());
        }

        @Override
        public T get() {
            return element;
        }
    }
}
