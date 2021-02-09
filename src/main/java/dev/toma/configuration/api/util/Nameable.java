package dev.toma.configuration.api.util;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Toma
 */
public interface Nameable {

    /**
     * Used as identifier/key
     * @return an unformatted name
     */
    String getUnformattedName();

    /**
     * Used inside UIs
     * @return a formatted name
     */
    String getFormattedName();

    /**
     * Useful class for wrapping objects as Nameable instances
     * when these objects are defined by libraries
     * @param <T> Object type
     */
    class Wrapped<T> implements Nameable, Supplier<T> {

        final Function<T, String> toStringFunction;
        final Function<String, String> formattingFunction;
        final T element;

        /**
         * Creates array of Wrapped objects
         *
         * @param input Array to wrap
         * @param toStringFunction Function to get unique key from object
         * @param <T> Object type
         * @return Wrapped array
         */
        public static <T> Wrapped<T>[] asArray(T[] input, Function<T, String> toStringFunction) {
            return asArray(input, toStringFunction, Function.identity());
        }

        /**
         * Creates array of Wrapped objects
         *
         * @param input Array to wrap
         * @param toStringFunction Function to get unique key from object
         * @param formattingFunction Function to get formatted string from object
         * @param <T> Object type
         * @return Wrapped array
         */
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
