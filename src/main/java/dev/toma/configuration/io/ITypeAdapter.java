package dev.toma.configuration.io;

import java.lang.reflect.Field;

public interface ITypeAdapter<T> {

    T fromField(Field field, ConfigLoadingContext context) throws Exception;

    default boolean isContainer() {
        return false;
    }
}
