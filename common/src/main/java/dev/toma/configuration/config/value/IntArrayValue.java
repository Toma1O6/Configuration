package dev.toma.configuration.config.value;

import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Field;

public class IntArrayValue extends NumericArrayValue<Integer> {

    public IntArrayValue(ValueData<Integer[]> valueData) {
        super(valueData, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public ValueRange<Integer> getValueRange(Field field, Integer typeMin, Integer typeMax) {
        Configurable.Range intRange = field.getAnnotation(Configurable.Range.class);
        return intRange != null
                ? new ValueRange<>((int) intRange.min(), (int) intRange.max())
                : new ValueRange<>(typeMin, typeMax);
    }

    @Override
    public Integer createElementInstance() {
        return 0;
    }

    @Override
    public void serialize(IConfigFormat format) {
        format.writeIntArray(this.getId(), this.get());
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.setValue(format.readIntArray(this.getId()));
    }

    public static final class Adapter extends TypeAdapter {

        @Override
        public void encodeToBuffer(ConfigValue<?> value, FriendlyByteBuf buffer) {
            saveToBuffer((Integer[]) value.get(), buffer, FriendlyByteBuf::writeInt);
        }

        @Override
        public Object decodeFromBuffer(ConfigValue<?> value, FriendlyByteBuf buffer) {
            return readFromBuffer(buffer, Integer[]::new, FriendlyByteBuf::readInt);
        }

        @Override
        public ConfigValue<?> serialize(String name, String[] comments, Object value, TypeSerializer serializer, AdapterContext context) throws IllegalAccessException {
            return new IntArrayValue(ValueData.of(name, (Integer[]) value, context, comments));
        }
    }
}
