package dev.toma.configuration.io;

import dev.toma.configuration.format.IFormattedWriter;

import java.lang.reflect.Field;

public interface ITypeAdapter<T> {

    T fromField(Field field, ConfigLoadingContext context) throws Exception;

    void write(IFormattedWriter writer, String field, T value);

    default boolean isContainer() {
        return false;
    }
}
