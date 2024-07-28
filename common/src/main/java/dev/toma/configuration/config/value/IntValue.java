package dev.toma.configuration.config.value;

import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.validate.NumberRange;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Field;

public class IntValue extends NumericValue<Integer> {

    public IntValue(ValueData<Integer> valueData) {
        super(valueData, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Override
    protected NumberRange<Integer> getValueRange(Field field, Integer min, Integer max) {
        Configurable.Range range = field.getAnnotation(Configurable.Range.class);
        return range != null
                ? NumberRange.interval(this, (int) Math.max(range.min(), min), (int) Math.min(range.max(), max))
                : NumberRange.all(this);
    }

    @Override
    public Integer getValueFromSlider(double sliderValue) {
        NumberRange<Integer> range = this.getRange();
        int delta = range.max() - range.min();
        return range.min() + (int) (delta * sliderValue);
    }

    @Override
    public void serialize(IConfigFormat format) {
        format.writeInt(this.getId(), this.get(Mode.SAVED));
    }

    @Override
    public void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.setValue(format.readInt(this.getId()));
    }

    public static final class Adapter extends TypeAdapter<Integer> {

        @Override
        public ConfigValue<Integer> serialize(TypeAttributes<Integer> attributes, Object instance, TypeSerializer serializer) {
            return new IntValue(ValueData.of(attributes));
        }

        @Override
        public void encodeToBuffer(ConfigValue<Integer> value, FriendlyByteBuf buffer) {
            buffer.writeInt(value.get());
        }

        @Override
        public Integer decodeFromBuffer(ConfigValue<Integer> value, FriendlyByteBuf buffer) {
            return buffer.readInt();
        }

        @Override
        public void setFieldValue(Field field, Object instance, Object value) throws IllegalAccessException {
            field.setInt(instance, (Integer) value);
        }
    }
}
