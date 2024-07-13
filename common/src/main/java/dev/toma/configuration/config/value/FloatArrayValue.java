package dev.toma.configuration.config.value;

import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.validate.NumberRange;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Field;

public class FloatArrayValue extends NumericArrayValue<Float> {

    public FloatArrayValue(ValueData<Float[]> valueData) {
        super(valueData, -Float.MAX_VALUE, Float.MAX_VALUE);
    }

    @Override
    public NumberRange<Float> getValueRange(Field field) {
        Configurable.DecimalRange decimalRange = field.getAnnotation(Configurable.DecimalRange.class);
        return decimalRange != null
                ? NumberRange.interval(this, (float) decimalRange.min(), (float) decimalRange.max())
                : NumberRange.all(this);
    }

    @Override
    public Float createElementInstance() {
        return 0.0F;
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.writeFloatArray(this.getId(), this.get());
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.setValue(format.readFloatArray(this.getId()));
    }

    @SuppressWarnings("unchecked")
    public static final class Adapter extends TypeAdapter {

        @Override
        public void encodeToBuffer(ConfigValue<?> value, FriendlyByteBuf buffer) {
            saveToBuffer((Float[]) value.get(), buffer, FriendlyByteBuf::writeFloat);
        }

        @Override
        public Object decodeFromBuffer(ConfigValue<?> value, FriendlyByteBuf buffer) {
            return readFromBuffer(buffer, Float[]::new, FriendlyByteBuf::readFloat);
        }

        @Override
        public ConfigValue<?> serialize(TypeAttributes<?> attributes, Object instance, TypeSerializer serializer) throws IllegalAccessException {
            return new FloatArrayValue(ValueData.of((TypeAttributes<Float[]>) attributes));
        }
    }
}
