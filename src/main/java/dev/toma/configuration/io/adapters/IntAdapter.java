package dev.toma.configuration.io.adapters;

import dev.toma.configuration.io.ConfigLoadingContext;
import dev.toma.configuration.io.ITypeAdapter;

import java.lang.reflect.Field;

public class IntAdapter implements ITypeAdapter<Integer> {

    @Override
    public Integer fromField(Field field, ConfigLoadingContext context) throws Exception {
        return field.getInt(context.getInstance());
    }
}
