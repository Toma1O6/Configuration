package dev.toma.configuration.config.value;

import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.exception.ConfigValueMissingException;

import java.lang.reflect.Field;

public class StringValue extends ConfigValue<String> {

    public StringValue(ValueData<String> valueData) {
        super(valueData);
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.writeString(this.getId(), this.get());
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.set(format.readString(this.getId()));
    }

    public static final class Adapter extends TypeAdapter {

        public Adapter() {
            super("string");
        }

        @Override
        public boolean isTargetType(Class<?> type) {
            return type.equals(String.class);
        }

        @Override
        public ConfigValue<?> serialize(String name, String[] comments, Object value, TypeSerializer serializer, SetField setter) throws IllegalAccessException {
            return new StringValue(ValueData.of(name, (String) value, setter, comments));
        }
    }
}
