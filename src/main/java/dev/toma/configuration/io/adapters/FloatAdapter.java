package dev.toma.configuration.io.adapters;

import dev.toma.configuration.format.IFormattedWriter;
import dev.toma.configuration.io.ConfigLoadingContext;
import dev.toma.configuration.io.ITypeAdapter;

import java.lang.reflect.Field;

public class FloatAdapter implements ITypeAdapter<Float> {

    @Override
    public Float fromField(Field field, ConfigLoadingContext context) throws Exception {
        return field.getFloat(context.getInstance());
    }

    @Override
    public void write(IFormattedWriter writer, String field, Float value) {
        writer.writeFloat(field, value);
    }
}
