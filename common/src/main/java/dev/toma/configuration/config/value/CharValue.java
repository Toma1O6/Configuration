package dev.toma.configuration.config.value;

import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Field;

public class CharValue extends ConfigValue<Character> {

    public CharValue(ValueData<Character> valueData) {
        super(valueData);
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.writeChar(this.getId(), this.get());
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.setValue(format.readChar(this.getId()));
    }

    @SuppressWarnings("unchecked")
    public static final class Adapter extends TypeAdapter {

        @Override
        public ConfigValue<?> serialize(TypeAttributes<?> attributes, Object instance, TypeSerializer serializer) throws IllegalAccessException {
            return new CharValue(ValueData.of((TypeAttributes<Character>) attributes));
        }

        @Override
        public void encodeToBuffer(ConfigValue<?> value, FriendlyByteBuf buffer) {
            buffer.writeChar((Integer) value.get());
        }

        @Override
        public Object decodeFromBuffer(ConfigValue<?> value, FriendlyByteBuf buffer) {
            return buffer.readChar();
        }

        @Override
        public void setFieldValue(Field field, Object instance, Object value) throws IllegalAccessException {
            field.setChar(instance, (char) value);
        }
    }
}
