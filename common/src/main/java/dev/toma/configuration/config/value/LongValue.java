package dev.toma.configuration.config.value;

import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.validate.NumberRange;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Field;

public class LongValue extends NumericValue<Long> {

    public LongValue(ValueData<Long> valueData) {
        super(valueData, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    @Override
    protected NumberRange<Long> getValueRange(Field field, Long min, Long max) {
        Configurable.Range range = field.getAnnotation(Configurable.Range.class);
        return range != null
                ? NumberRange.interval(this, range.min(), range.max())
                : NumberRange.all(this);
    }

    @Override
    public Long getValueFromSlider(double sliderValue) {
        NumberRange<Long> range = this.getRange();
        long delta = range.max() - range.min();
        return range.min() + (long) (delta * sliderValue);
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.writeLong(this.getId(), this.get(Mode.SAVED));
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.setValue(format.readLong(this.getId()));
    }

    @SuppressWarnings("unchecked")
    public static final class Adapter extends TypeAdapter<Long> {

        @Override
        public ConfigValue<Long> serialize(TypeAttributes<Long> attributes, Object instance, TypeSerializer serializer) throws IllegalAccessException {
            return new LongValue(ValueData.of(attributes));
        }

        @Override
        public void encodeToBuffer(ConfigValue<Long> value, FriendlyByteBuf buffer) {
            buffer.writeLong(value.get());
        }

        @Override
        public Long decodeFromBuffer(ConfigValue<Long> value, FriendlyByteBuf buffer) {
            return buffer.readLong();
        }

        @Override
        public void setFieldValue(Field field, Object instance, Object value) throws IllegalAccessException {
            field.setLong(instance, (Long) value);
        }
    }
}
