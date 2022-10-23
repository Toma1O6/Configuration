package dev.toma.configuration.io.adapters;

import dev.toma.configuration.io.ConfigLoadingContext;
import dev.toma.configuration.io.ITypeAdapter;

import java.lang.reflect.Field;

public class BoolAdapter implements ITypeAdapter<Boolean> {

    @Override
    public Boolean fromField(Field field, ConfigLoadingContext context) throws Exception {
        Object instance = context.getInstance();
        return field.getBoolean(instance);
    }
}
