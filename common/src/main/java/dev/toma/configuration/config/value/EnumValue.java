package dev.toma.configuration.config.value;

import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Arrays;
import java.util.List;

public class EnumValue<E extends Enum<E>> extends ConfigValue<E> {

    public EnumValue(ValueData<E> valueData) {
        super(valueData);
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.addComments(getValidEnumEntriesAsString(this.valueData.getValueType()));
        format.writeEnum(this.getId(), this.get(Mode.SAVED));
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.setValue(format.readEnum(this.getId(), getValueType()));
    }

    static <E extends Enum<E>> String getValidEnumEntriesAsString(Class<E> enumType) {
        List<String> values = Arrays.stream(enumType.getEnumConstants())
                .map(Enum::name).toList();
        return "Allowed values: [" + String.join(", ", values) + "]";
    }

    public static final class Adapter<E extends Enum<E>> extends TypeAdapter<E> {

        @Override
        public ConfigValue<E> serialize(TypeAttributes<E> attributes, Object instance, TypeSerializer serializer) throws IllegalAccessException {
            return new EnumValue<>(ValueData.of(attributes));
        }

        @Override
        public void encodeToBuffer(ConfigValue<E> value, FriendlyByteBuf buffer) {
            buffer.writeEnum(value.get());
        }

        @Override
        public E decodeFromBuffer(ConfigValue<E> value, FriendlyByteBuf buffer) {
            Class<E> type = value.getValueType();
            return buffer.readEnum(type);
        }
    }
}
