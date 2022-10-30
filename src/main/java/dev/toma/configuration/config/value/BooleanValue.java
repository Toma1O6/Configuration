package dev.toma.configuration.config.value;

import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.exception.ConfigValueMissingException;

import java.lang.reflect.Field;

public final class BooleanValue extends ConfigValue<Boolean> {

    public BooleanValue(ValueData<Boolean> valueData) {
        super(valueData);
    }

    @Override
    public void serialize(IConfigFormat format) {
        boolean value = this.get();
        format.writeBoolean(this.getId(), value);
    }

    @Override
    public void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        String field = this.getId();
        this.set(format.readBoolean(field));
    }

    public static class Adapter extends TypeAdapter {

        public Adapter() {
            super("boolean");
        }

        @Override
        public boolean isTargetType(Class<?> type) {
            return type.equals(Boolean.TYPE);
        }

        @Override
        public ConfigValue<?> serialize(String name, String[] comments, Object value, TypeSerializer serializer, SetField setter) {
            return new BooleanValue(ValueData.of(name, (boolean) value, setter, comments));
        }

        @Override
        public void setFieldValue(Field field, Object instance, Object value) throws IllegalAccessException {
            field.setBoolean(instance, (boolean) value);
        }
    }
}
