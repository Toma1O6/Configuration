package dev.toma.configuration.config.value;

import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.exception.ConfigValueMissingException;

import java.lang.reflect.Field;

public final class CharValue extends ConfigValue<Character> {

    public CharValue(ValueData<Character> valueData) {
        super(valueData);
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.writeChar(this.getId(), this.get());
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.set(format.readChar(this.getId()));
    }

    public static final class Adapter extends TypeAdapter {

        public Adapter() {
            super("char");
        }

        @Override
        public boolean isTargetType(Class<?> type) {
            return type.equals(Character.TYPE);
        }

        @Override
        public ConfigValue<?> serialize(String name, String[] comments, Object value, TypeSerializer serializer, SetField setter) throws IllegalAccessException {
            return new CharValue(ValueData.of(name, (char) value, setter, comments));
        }

        @Override
        public void setFieldValue(Field field, Object instance, Object value) throws IllegalAccessException {
            field.setChar(instance, (char) value);
        }
    }
}
