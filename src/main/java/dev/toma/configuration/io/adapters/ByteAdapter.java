package dev.toma.configuration.io.adapters;

import dev.toma.configuration.format.IFormattedWriter;
import dev.toma.configuration.io.ConfigLoadingContext;
import dev.toma.configuration.io.ITypeAdapter;

import java.lang.reflect.Field;

public class ByteAdapter implements ITypeAdapter<Byte> {

    @Override
    public Byte fromField(Field field, ConfigLoadingContext context) throws Exception {
        return field.getByte(context.getInstance());
    }

    @Override
    public void write(IFormattedWriter writer, String field, Byte value) {
        writer.writeByte(field, value);
    }
}
