package dev.toma.configuration.config.value;

import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import net.minecraft.network.FriendlyByteBuf;

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
        this.setValue(format.readEnum(this.getId(), getValueType()));
    }

    @SuppressWarnings("unchecked")
    public static final class Adapter<E extends Enum<E>> extends TypeAdapter {

        @Override
        public ConfigValue<?> serialize(TypeAttributes<?> attributes, Object instance, TypeSerializer serializer) throws IllegalAccessException {
            return new EnumValue<>(ValueData.of((TypeAttributes<E>) attributes));
        }

        @Override
        public void encodeToBuffer(ConfigValue<?> value, FriendlyByteBuf buffer) {
            buffer.writeEnum((E) value.get());
        }

        @Override
        public Object decodeFromBuffer(ConfigValue<?> value, FriendlyByteBuf buffer) {
            Class<E> type = (Class<E>) value.getValueType();
            return buffer.readEnum(type);
        }
    }
}
