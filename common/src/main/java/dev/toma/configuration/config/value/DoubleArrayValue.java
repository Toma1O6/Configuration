package dev.toma.configuration.config.value;

import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.validate.NumberRange;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Field;

public class DoubleArrayValue extends NumericArrayValue<Double> {

    public DoubleArrayValue(ValueData<Double[]> valueData) {
        super(valueData, -Double.MAX_VALUE, Double.MAX_VALUE);
    }

    @Override
    public NumberRange<Double> getValueRange(Field field) {
        Configurable.DecimalRange decimalRange = field.getAnnotation(Configurable.DecimalRange.class);
        return decimalRange != null
                ? NumberRange.interval(this, decimalRange.min(), decimalRange.max())
                : NumberRange.all(this);
    }

    @Override
    public Double createElementInstance() {
        return 0.0;
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.writeDoubleArray(this.getId(), this.get());
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.setValue(format.readDoubleArray(this.getId()));
    }

    @SuppressWarnings("unchecked")
    public static final class Adapter extends TypeAdapter {

        @Override
        public void encodeToBuffer(ConfigValue<?> value, FriendlyByteBuf buffer) {
            saveToBuffer((Double[]) value.get(), buffer, FriendlyByteBuf::writeDouble);
        }

        @Override
        public Object decodeFromBuffer(ConfigValue<?> value, FriendlyByteBuf buffer) {
            return readFromBuffer(buffer, Double[]::new, FriendlyByteBuf::readDouble);
        }

        @Override
        public ConfigValue<?> serialize(TypeAttributes<?> attributes, Object instance, TypeSerializer serializer) throws IllegalAccessException {
            return new DoubleArrayValue(ValueData.of((TypeAttributes<Double[]>) attributes));
        }
    }
}
