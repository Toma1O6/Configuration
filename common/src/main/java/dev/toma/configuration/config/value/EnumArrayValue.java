package dev.toma.configuration.config.value;

import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Array;

public class EnumArrayValue<E extends Enum<E>> extends AbstractArrayValue<E> {

    public EnumArrayValue(ValueData<E[]> value) {
        super(value);
    }

    @Override
    public E createElementInstance() {
        Class<E> enumType = this.getElementType();
        E[] constants = enumType.getEnumConstants();
        if (constants.length == 0) {
            throw new IllegalArgumentException("Enum does not define any constants");
        }
        return constants[0];
    }

    @SuppressWarnings("unchecked")
    public Class<E> getElementType() {
        return (Class<E>) this.valueData.getValueType().getComponentType();
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.writeEnumArray(getId(), get(Mode.SAVED));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        Class<E> type = (Class<E>) getValueType().getComponentType();
        setValue(format.readEnumArray(getId(), type));
    }

    @SuppressWarnings("unchecked")
    public static final class Adapter<E extends Enum<E>> extends TypeAdapter<E[]> {

        @Override
        public ConfigValue<E[]> serialize(TypeAttributes<E[]> attributes, Object instance, TypeSerializer serializer) throws IllegalAccessException {
            return new EnumArrayValue<>(ValueData.of(attributes));
        }

        @Override
        public void encodeToBuffer(ConfigValue<E[]> value, FriendlyByteBuf buffer) {
            E[] values = value.get();
            buffer.writeInt(values.length);
            for (E e : values) {
                buffer.writeEnum(e);
            }
        }

        @Override
        public E[] decodeFromBuffer(ConfigValue<E[]> value, FriendlyByteBuf buffer) {
            int count = buffer.readInt();
            Class<E> type = (Class<E>) value.getValueType().getComponentType();
            E[] enumArray = (E[]) Array.newInstance(type, count);
            for (int i = 0; i < count; i++) {
                enumArray[i] = buffer.readEnum(type);
            }
            return enumArray;
        }
    }
}
