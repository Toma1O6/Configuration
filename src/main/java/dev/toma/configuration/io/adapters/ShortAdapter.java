package dev.toma.configuration.io.adapters;

import dev.toma.configuration.format.IFormattedWriter;
import dev.toma.configuration.io.ConfigLoadingContext;
import dev.toma.configuration.io.ITypeAdapter;

import java.lang.reflect.Field;

public class ShortAdapter implements ITypeAdapter<Short> {

    @Override
    public Short fromField(Field field, ConfigLoadingContext context) throws Exception {
        return field.getShort(context.getInstance());
    }

    @Override
    public void write(IFormattedWriter writer, String field, Short value) {
        writer.writeShort(field, value);
    }
}
