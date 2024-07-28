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
        Configurable.Range range = field.getAnnotation(Configurable.Range.class);
        return range != null
                ? NumberRange.interval(this, (int) Math.max(range.min(), min()), (int) Math.min(range.max(), max()))
                : NumberRange.all(this);
    }

    @Override
    public Integer createElementInstance() {
        return 0;
    }

    @Override
    public void serialize(IConfigFormat format) {
        format.writeIntArray(this.getId(), this.get(Mode.SAVED));
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.setValue(format.readIntArray(this.getId()));
    }

    public static final class Adapter extends TypeAdapter<Integer[]> {

        @Override
        public void encodeToBuffer(ConfigValue<Integer[]> value, FriendlyByteBuf buffer) {
            saveToBuffer(value.get(), buffer, FriendlyByteBuf::writeInt);
        }

        @Override
        public Integer[] decodeFromBuffer(ConfigValue<Integer[]> value, FriendlyByteBuf buffer) {
            return readFromBuffer(buffer, Integer[]::new, FriendlyByteBuf::readInt);
        }

        @Override
        public ConfigValue<Integer[]> serialize(TypeAttributes<Integer[]> attributes, Object instance, TypeSerializer serializer) throws IllegalAccessException {
            return new IntArrayValue(ValueData.of(attributes));
        }
    }
}
