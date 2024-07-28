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
        format.writeChar(this.getId(), this.get(Mode.SAVED));
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.setValue(format.readChar(this.getId()));
    }

    public static final class Adapter extends TypeAdapter<Character> {

        @Override
        public ConfigValue<Character> serialize(TypeAttributes<Character> attributes, Object instance, TypeSerializer serializer) throws IllegalAccessException {
            return new CharValue(ValueData.of(attributes));
        }

        @Override
        public void encodeToBuffer(ConfigValue<Character> value, FriendlyByteBuf buffer) {
            buffer.writeChar(value.get());
        }

        @Override
        public Character decodeFromBuffer(ConfigValue<Character> value, FriendlyByteBuf buffer) {
            return buffer.readChar();
        }

        @Override
        public void setFieldValue(Field field, Object instance, Object value) throws IllegalAccessException {
            field.setChar(instance, (char) value);
        }
    }
}
