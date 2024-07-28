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
        boolean value = this.get(Mode.SAVED);
        format.writeBoolean(this.getId(), value);
    }

    @Override
    public void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        String field = this.getId();
        this.setValue(format.readBoolean(field));
    }

    public static class Adapter extends TypeAdapter<Boolean> {

        @Override
        public ConfigValue<Boolean> serialize(TypeAttributes<Boolean> attributes, Object instance, TypeSerializer serializer) {
            return new BooleanValue(ValueData.of(attributes));
        }

        @Override
        public void encodeToBuffer(ConfigValue<Boolean> value, FriendlyByteBuf buffer) {
            buffer.writeBoolean(value.get());
        }

        @Override
        public Boolean decodeFromBuffer(ConfigValue<Boolean> value, FriendlyByteBuf buffer) {
            return buffer.readBoolean();
        }

        @Override
        public void setFieldValue(Field field, Object instance, Object value) throws IllegalAccessException {
            field.setBoolean(instance, (boolean) value);
        }
    }
}
