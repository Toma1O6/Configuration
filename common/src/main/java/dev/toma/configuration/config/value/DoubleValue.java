package dev.toma.configuration.config.value;

import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.validate.NumberRange;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Field;

public class DoubleValue extends NumericValue<Double> {

    public DoubleValue(ValueData<Double> valueData) {
        super(valueData, -Double.MAX_VALUE, Double.MAX_VALUE);
    }

    @Override
    protected NumberRange<Double> getValueRange(Field field, Double min, Double max) {
        Configurable.DecimalRange range = field.getAnnotation(Configurable.DecimalRange.class);
        return range != null
                ? NumberRange.interval(this, range.min(), range.max())
                : NumberRange.all(this);
    }

    @Override
    public Double getValueFromSlider(double sliderValue) {
        NumberRange<Double> range = this.getRange();
        double delta = range.max() - range.min();
        return range.min() + delta * sliderValue;
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.writeDouble(this.getId(), this.get(Mode.SAVED));
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.setValue(format.readDouble(this.getId()));
    }

    public static final class Adapter extends TypeAdapter<Double> {

        @Override
        public ConfigValue<Double> serialize(TypeAttributes<Double> attributes, Object instance, TypeSerializer serializer) throws IllegalAccessException {
            return new DoubleValue(ValueData.of(attributes));
        }

        @Override
        public void encodeToBuffer(ConfigValue<Double> value, FriendlyByteBuf buffer) {
            buffer.writeDouble(value.get());
        }

        @Override
        public Double decodeFromBuffer(ConfigValue<Double> value, FriendlyByteBuf buffer) {
            return buffer.readDouble();
        }

        @Override
        public void setFieldValue(Field field, Object instance, Object value) throws IllegalAccessException {
            field.setDouble(instance, (Double) value);
        }
    }
}
