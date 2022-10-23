package dev.toma.configuration.io.adapters;

import dev.toma.configuration.io.ConfigLoadingContext;
import dev.toma.configuration.io.ITypeAdapter;

import java.lang.reflect.Field;

public class DoubleAdapter implements ITypeAdapter<Double> {

    @Override
    public Double fromField(Field field, ConfigLoadingContext context) throws Exception {
        return field.getDouble(context.getInstance());
    }
}
