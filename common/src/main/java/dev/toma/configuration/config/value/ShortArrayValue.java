package dev.toma.configuration.config.value;

import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.validate.NumberRange;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Field;

public class ShortArrayValue extends NumericArrayValue<Short> {

    public ShortArrayValue(ValueData<Short[]> valueData) {
        super(valueData, Short.MIN_VALUE, Short.MAX_VALUE);
    }

    @Override
    public NumberRange<Short> getValueRange(Field field) {
        Configurable.Range range = field.getAnnotation(Configurable.Range.class);
        return range != null
                ? NumberRange.interval(this, (short) Math.max(range.min(), min()), (short) Math.min(range.max(), max()))
                : NumberRange.all(this);
    }

    @Override
    public Short createElementInstance() {
        return 0;
    }

    @Override
    public void serialize(IConfigFormat format) {
        format.writeShortArray(this.getId(), this.get(Mode.SAVED));
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.setValue(format.readShortArray(this.getId()));
    }

    public static final class Adapter extends TypeAdapter<Short[]> {

        @Override
        public void encodeToBuffer(ConfigValue<Short[]> value, FriendlyByteBuf buffer) {
            saveToBuffer(value.get(), buffer, (buf, aShort) -> buf.writeShort(aShort));
        }

        @Override
        public Short[] decodeFromBuffer(ConfigValue<Short[]> value, FriendlyByteBuf buffer) {
            return readFromBuffer(buffer, Short[]::new, FriendlyByteBuf::readShort);
        }

        @Override
        public ConfigValue<Short[]> serialize(TypeAttributes<Short[]> attributes, Object instance, TypeSerializer serializer) throws IllegalAccessException {
            return new ShortArrayValue(ValueData.of(attributes));
        }
    }
}
