package dev.toma.configuration.io.adapters;

import dev.toma.configuration.io.ConfigLoadingContext;
import dev.toma.configuration.io.ITypeAdapter;

import java.lang.reflect.Field;

public class ByteAdapter implements ITypeAdapter<Byte> {

    @Override
    public Byte fromField(Field field, ConfigLoadingContext context) throws Exception {
        return field.getByte(context.getInstance());
    }
}
