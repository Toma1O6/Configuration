package dev.toma.configuration.config.value;

import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.validate.NumberRange;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Field;

public class ByteValue extends NumericValue<Byte> {

    public ByteValue(ValueData<Byte> data) {
        super(data, Byte.MIN_VALUE, Byte.MAX_VALUE);
    }

    @Override
    protected NumberRange<Byte> getValueRange(Field field, Byte min, Byte max) {
        Configurable.Range range = field.getAnnotation(Configurable.Range.class);
        return range != null
                ? NumberRange.interval(this, (byte) Math.max(range.min(), min), (byte) Math.min(range.max(), max))
                : NumberRange.all(this);
    }

    @Override
    public Byte getValueFromSlider(double sliderValue) {
        NumberRange<Byte> range = this.getRange();
        int delta = range.max() - range.min();
        return (byte) (range.min() + (byte) (delta * sliderValue));
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.writeByte(this.getId(), this.get(Mode.SAVED));
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.setValue(format.readByte(this.getId()));
    }

    public static final class Adapter extends TypeAdapter<Byte> {

        @Override
        public ConfigValue<Byte> serialize(TypeAttributes<Byte> attributes, Object instance, TypeSerializer serializer) throws IllegalAccessException {
            return new ByteValue(ValueData.of(attributes));
        }

        @Override
        public void encodeToBuffer(ConfigValue<Byte> value, FriendlyByteBuf buffer) {
            buffer.writeByte(value.get());
        }

        @Override
        public Byte decodeFromBuffer(ConfigValue<Byte> value, FriendlyByteBuf buffer) {
            return buffer.readByte();
        }

        @Override
        public void setFieldValue(Field field, Object instance, Object value) throws IllegalAccessException {
            field.setByte(instance, (Byte) value);
        }
    }
}
