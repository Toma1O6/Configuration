package dev.toma.configuration.io.adapters;

import dev.toma.configuration.format.IFormattedWriter;
import dev.toma.configuration.io.ConfigLoadingContext;
import dev.toma.configuration.io.ITypeAdapter;

import java.lang.reflect.Field;

public class StringAdapter implements ITypeAdapter<String> {

    @Override
    public String fromField(Field field, ConfigLoadingContext context) throws Exception {
        return (String) field.get(context.getInstance());
    }

    @Override
    public void write(IFormattedWriter writer, String field, String value) {
        writer.writeString(field, value);
    }
}
