package dev.toma.configuration.config.value;

import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.exception.ConfigValueMissingException;

public class IntArrayValue extends ConfigValue<int[]> {

    public IntArrayValue(ValueData<int[]> valueData) {
        super(valueData);
    }

    @Override
    public void serialize(IConfigFormat format) {
        format.writeIntArray(this.getId(), this.get());
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.set(format.readIntArray(this.getId()));
    }

    public static final class Adapter extends TypeAdapter {

        public Adapter() {
            super("int[]");
        }

        @Override
        public boolean isTargetType(Class<?> type) {
            return type.equals(int[].class);
        }

        @Override
        public ConfigValue<?> serialize(String name, String[] comments, Object value, TypeSerializer serializer, SetField setter) throws IllegalAccessException {
            return new IntArrayValue(ValueData.of(name, (int[]) value, setter, comments));
        }
    }
}
