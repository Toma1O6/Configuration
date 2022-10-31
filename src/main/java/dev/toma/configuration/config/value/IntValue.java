package dev.toma.configuration.config.value;

import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import net.minecraft.network.PacketBuffer;

import java.lang.reflect.Field;

public final class IntValue extends ConfigValue<Integer> {

    public IntValue(ValueData<Integer> valueData) {
        super(valueData);
    }

    @Override
    public void serialize(IConfigFormat format) {
        format.writeInt(this.getId(), this.get());
    }

    @Override
    public void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.set(format.readInt(this.getId()));
    }

    public static final class Adapter extends TypeAdapter {

        public Adapter() {
            super("int");
        }

        @Override
        public boolean isTargetType(Class<?> type) {
            return type.equals(Integer.TYPE);
        }

        @Override
        public ConfigValue<?> serialize(String name, String[] comments, Object value, TypeSerializer serializer, AdapterContext context) {
            return new IntValue(ValueData.of(name, (int) value, context, comments));
        }

        @Override
        public void encodeToBuffer(ConfigValue<?> value, PacketBuffer buffer) {
            buffer.writeInt((Integer) value.get());
        }

        @Override
        public Object decodeFromBuffer(ConfigValue<?> value, PacketBuffer buffer) {
            return buffer.readInt();
        }

        @Override
        public void setFieldValue(Field field, Object instance, Object value) throws IllegalAccessException {
            field.setInt(instance, (Integer) value);
        }
    }
}
