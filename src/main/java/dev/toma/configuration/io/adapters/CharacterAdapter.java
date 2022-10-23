package dev.toma.configuration.io.adapters;

import dev.toma.configuration.format.IFormattedWriter;
import dev.toma.configuration.io.ConfigLoadingContext;
import dev.toma.configuration.io.ITypeAdapter;

import java.lang.reflect.Field;

public class CharacterAdapter implements ITypeAdapter<Character> {

    @Override
    public Character fromField(Field field, ConfigLoadingContext context) throws Exception {
        return field.getChar(context.getInstance());
    }

    @Override
    public void write(IFormattedWriter writer, String field, Character value) {
        writer.writeChar(field, value);
    }
}
