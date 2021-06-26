package dev.toma.configuration.util;

public class ArrayUtils {

    public static <T> int indexOf_ref(T element, T[] values) {
        for (int i = 0; i < values.length; i++) {
            if (values[i] == element)
                return i;
        }
        return -1;
    }

    public static int indexOf_eq(Object element, Object[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(element))
                return i;
        }
        return -1;
    }
}
