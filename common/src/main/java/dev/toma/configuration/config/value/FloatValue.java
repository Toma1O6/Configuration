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
                ? NumberRange.interval(this, (float) range.min(), (float) range.max())
                : NumberRange.all(this);
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.writeFloat(this.getId(), this.get());
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.setValue(format.readFloat(this.getId()));
    }

    @SuppressWarnings("unchecked")
    public static final class Adapter extends TypeAdapter {

        @Override
        public ConfigValue<?> serialize(TypeAttributes<?> attributes, Object instance, TypeSerializer serializer) throws IllegalAccessException {
            return new FloatValue(ValueData.of((TypeAttributes<Float>) attributes));
        }

        @Override
        public void encodeToBuffer(ConfigValue<?> value, FriendlyByteBuf buffer) {
            buffer.writeFloat((Float) value.get());
        }

        @Override
        public Object decodeFromBuffer(ConfigValue<?> value, FriendlyByteBuf buffer) {
            return buffer.readFloat();
        }

        @Override
        public void setFieldValue(Field field, Object instance, Object value) throws IllegalAccessException {
            field.setFloat(instance, (Float) value);
        }
    }
}
