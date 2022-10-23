package dev.toma.configuration.io.adapters;

import dev.toma.configuration.io.ConfigLoadingContext;
import dev.toma.configuration.io.ITypeAdapter;

import java.lang.reflect.Field;

public class LongAdapter implements ITypeAdapter<Long> {

    @Override
    public Long fromField(Field field, ConfigLoadingContext context) throws Exception {
        return field.getLong(context.getInstance());
    }
}
