package dev.toma.configuration.config.value;

import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.validate.NumberRange;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Field;

public class FloatValue extends NumericValue<Float> {

    public FloatValue(ValueData<Float> valueData) {
        super(valueData, -Float.MAX_VALUE, Float.MAX_VALUE);
    }

    @Override
    protected NumberRange<Float> getValueRange(Field field, Float min, Float max) {
        Configurable.DecimalRange range = field.getAnnotation(Configurable.DecimalRange.class);
        return range != null
                ? NumberRange.interval(this, (float) Math.max(min, range.min()), (float) Math.min(range.max(), max))
                : NumberRange.all(this);
    }

    @Override
    public Float getValueFromSlider(double sliderValue) {
        NumberRange<Float> range = this.getRange();
        float delta = range.max() - range.min();
        return range.min() + (float) (delta * sliderValue);
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.writeFloat(this.getId(), this.get(Mode.SAVED));
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.setValue(format.readFloat(this.getId()));
    }

    public static final class Adapter extends TypeAdapter<Float> {

        @Override
        public ConfigValue<Float> serialize(TypeAttributes<Float> attributes, Object instance, TypeSerializer serializer) throws IllegalAccessException {
            return new FloatValue(ValueData.of(attributes));
        }

        @Override
        public void encodeToBuffer(ConfigValue<Float> value, FriendlyByteBuf buffer) {
            buffer.writeFloat(value.get());
        }

        @Override
        public Float decodeFromBuffer(ConfigValue<Float> value, FriendlyByteBuf buffer) {
            return buffer.readFloat();
        }

        @Override
        public void setFieldValue(Field field, Object instance, Object value) throws IllegalAccessException {
            field.setFloat(instance, (Float) value);
        }
    }
}
