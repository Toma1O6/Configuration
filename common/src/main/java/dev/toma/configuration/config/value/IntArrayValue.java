package dev.toma.configuration.config.value;

import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.validate.NumberRange;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Field;

public class IntArrayValue extends NumericArrayValue<Integer> {

    public IntArrayValue(ValueData<Integer[]> valueData) {
        super(valueData, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public NumberRange<Integer> getValueRange(Field field) {
        Configurable.Range intRange = field.getAnnotation(Configurable.Range.class);
        return intRange != null
                ? NumberRange.interval(this, (int) intRange.min(), (int) intRange.max())
                : NumberRange.all(this);
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

    @SuppressWarnings("unchecked")
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
        public ConfigValue<?> serialize(TypeAttributes<?> attributes, Object instance, TypeSerializer serializer) throws IllegalAccessException {
            return new IntArrayValue(ValueData.of((TypeAttributes<Integer[]>) attributes));
        }
    }
}
