package dev.toma.configuration.config.value;

import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Field;

public class BooleanValue extends ConfigValue<Boolean> {

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
        this.setValue(format.readBoolean(field));
    }

    @SuppressWarnings("unchecked")
    public static class Adapter extends TypeAdapter {

        @Override
        public ConfigValue<?> serialize(TypeAttributes<?> attributes, Object instance, TypeSerializer serializer) {
            return new BooleanValue(ValueData.of((TypeAttributes<Boolean>) attributes));
        }

        @Override
        public void encodeToBuffer(ConfigValue<?> value, FriendlyByteBuf buffer) {
            buffer.writeBoolean((Boolean) value.get());
        }

        @Override
        public Object decodeFromBuffer(ConfigValue<?> value, FriendlyByteBuf buffer) {
            return buffer.readBoolean();
        }

        @Override
        public void setFieldValue(Field field, Object instance, Object value) throws IllegalAccessException {
            field.setBoolean(instance, (boolean) value);
        }
    }
}
