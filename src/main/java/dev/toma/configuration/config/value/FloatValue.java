package dev.toma.configuration.config.value;

import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import net.minecraft.network.PacketBuffer;

import java.lang.reflect.Field;

public class FloatValue extends ConfigValue<Float> {

    public FloatValue(ValueData<Float> valueData) {
        super(valueData);
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.writeFloat(this.getId(), this.get());
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.set(format.readFloat(this.getId()));
    }

    public static final class Adapter extends TypeAdapter {

        public Adapter() {
            super("float");
        }

        @Override
        public boolean isTargetType(Class<?> type) {
            return type.equals(Float.TYPE);
        }

        @Override
        public ConfigValue<?> serialize(String name, String[] comments, Object value, TypeSerializer serializer, AdapterContext context) throws IllegalAccessException {
            return new FloatValue(ValueData.of(name, (float) value, context, comments));
        }

        @Override
        public void encodeToBuffer(ConfigValue<?> value, PacketBuffer buffer) {
            buffer.writeFloat((Float) value.get());
        }

        @Override
        public Object decodeFromBuffer(ConfigValue<?> value, PacketBuffer buffer) {
            return buffer.readFloat();
        }

        @Override
        public void setFieldValue(Field field, Object instance, Object value) throws IllegalAccessException {
            field.setFloat(instance, (Float) value);
        }
    }
}
