package dev.toma.configuration.config.value;

import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.validate.NumberRange;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Field;

public class ShortValue extends NumericValue<Short> {

    public ShortValue(ValueData<Short> data) {
        super(data, Short.MIN_VALUE, Short.MAX_VALUE);
    }

    @Override
    protected NumberRange<Short> getValueRange(Field field, Short min, Short max) {
        Configurable.Range range = field.getAnnotation(Configurable.Range.class);
        return range != null
                ? NumberRange.interval(this, (short) Math.max(range.min(), min), (short) Math.min(range.max(), max))
                : NumberRange.all(this);
    }

    @Override
    public Short getValueFromSlider(double sliderValue) {
        NumberRange<Short> range = this.getRange();
        int delta = range.max() - range.min();
        return (short) (range.min() + (short) (delta * sliderValue));
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.writeShort(this.getId(), this.get(Mode.SAVED));
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.setValue(format.readShort(this.getId()));
    }

    public static final class Adapter extends TypeAdapter<Short> {

        @Override
        public ConfigValue<Short> serialize(TypeAttributes<Short> attributes, Object instance, TypeSerializer serializer) throws IllegalAccessException {
            return new ShortValue(ValueData.of(attributes));
        }

        @Override
        public void encodeToBuffer(ConfigValue<Short> value, FriendlyByteBuf buffer) {
            buffer.writeByte(value.get());
        }

        @Override
        public Short decodeFromBuffer(ConfigValue<Short> value, FriendlyByteBuf buffer) {
            return buffer.readShort();
        }

        @Override
        public void setFieldValue(Field field, Object instance, Object value) throws IllegalAccessException {
            field.setShort(instance, (Short) value);
        }
    }
}
