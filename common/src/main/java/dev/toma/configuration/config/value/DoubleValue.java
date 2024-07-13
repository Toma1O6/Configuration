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
    protected void serialize(IConfigFormat format) {
        format.writeDouble(this.getId(), this.get());
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.setValue(format.readDouble(this.getId()));
    }

    @SuppressWarnings("unchecked")
    public static final class Adapter extends TypeAdapter {

        @Override
        public ConfigValue<?> serialize(TypeAttributes<?> attributes, Object instance, TypeSerializer serializer) throws IllegalAccessException {
            return new DoubleValue(ValueData.of((TypeAttributes<Double>) attributes));
        }

        @Override
        public void encodeToBuffer(ConfigValue<?> value, FriendlyByteBuf buffer) {
            buffer.writeDouble((Double) value.get());
        }

        @Override
        public Object decodeFromBuffer(ConfigValue<?> value, FriendlyByteBuf buffer) {
            return buffer.readDouble();
        }

        @Override
        public void setFieldValue(Field field, Object instance, Object value) throws IllegalAccessException {
            field.setDouble(instance, (Double) value);
        }
    }
}
