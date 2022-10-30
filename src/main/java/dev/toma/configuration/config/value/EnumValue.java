package dev.toma.configuration.config.value;

import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.exception.ConfigValueMissingException;

public class EnumValue<E extends Enum<E>> extends ConfigValue<E> {

    public EnumValue(ValueData<E> valueData) {
        super(valueData);
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.writeEnum(this.getId(), this.get());
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.useDefaultValue();
        E en = this.get();
        this.set(format.readEnum(this.getId(), en.getDeclaringClass()));
    }

    public static final class Adapter<E extends Enum<E>> extends TypeAdapter {

        public Adapter() {
            super("enum");
        }

        @Override
        public boolean isTargetType(Class<?> type) {
            return type.isEnum();
        }

        @SuppressWarnings("unchecked")
        @Override
        public ConfigValue<?> serialize(String name, String[] comments, Object value, TypeSerializer serializer, SetField setter) throws IllegalAccessException {
            return new EnumValue<>(ValueData.of(name, (E) value, setter, comments));
        }

        @Override
        public int getPriorityIndex() {
            return 1;
        }
    }
}
