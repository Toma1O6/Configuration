package dev.toma.configuration.config.value;

import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import net.minecraft.network.PacketBuffer;

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

        @SuppressWarnings("unchecked")
        @Override
        public ConfigValue<?> serialize(String name, String[] comments, Object value, TypeSerializer serializer, AdapterContext context) throws IllegalAccessException {
            return new EnumValue<>(ValueData.of(name, (E) value, context, comments));
        }

        @SuppressWarnings("unchecked")
        @Override
        public void encodeToBuffer(ConfigValue<?> value, PacketBuffer buffer) {
            buffer.writeEnum((E) value.get());
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object decodeFromBuffer(ConfigValue<?> value, PacketBuffer buffer) {
            E e = (E) value.get();
            Class<E> eClass = e.getDeclaringClass();
            return buffer.readEnum(eClass);
        }
    }
}
