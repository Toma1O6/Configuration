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
                ? NumberRange.interval(this, (float) Math.max(min(), decimalRange.min()), (float) Math.min(decimalRange.max(), max()))
                : NumberRange.all(this);
    }

    @Override
    public Float createElementInstance() {
        return 0.0F;
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.writeFloatArray(this.getId(), this.get(Mode.SAVED));
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.setValue(format.readFloatArray(this.getId()));
    }

    public static final class Adapter extends TypeAdapter<Float[]> {

        @Override
        public void encodeToBuffer(ConfigValue<Float[]> value, FriendlyByteBuf buffer) {
            saveToBuffer(value.get(), buffer, FriendlyByteBuf::writeFloat);
        }

        @Override
        public Float[] decodeFromBuffer(ConfigValue<Float[]> value, FriendlyByteBuf buffer) {
            return readFromBuffer(buffer, Float[]::new, FriendlyByteBuf::readFloat);
        }

        @Override
        public ConfigValue<Float[]> serialize(TypeAttributes<Float[]> attributes, Object instance, TypeSerializer serializer) throws IllegalAccessException {
            return new FloatArrayValue(ValueData.of(attributes));
        }
    }
}
