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
        format.writeDoubleArray(this.getId(), this.get(Mode.SAVED));
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.setValue(format.readDoubleArray(this.getId()));
    }

    public static final class Adapter extends TypeAdapter<Double[]> {

        @Override
        public void encodeToBuffer(ConfigValue<Double[]> value, FriendlyByteBuf buffer) {
            saveToBuffer(value.get(), buffer, FriendlyByteBuf::writeDouble);
        }

        @Override
        public Double[] decodeFromBuffer(ConfigValue<Double[]> value, FriendlyByteBuf buffer) {
            return readFromBuffer(buffer, Double[]::new, FriendlyByteBuf::readDouble);
        }

        @Override
        public ConfigValue<Double[]> serialize(TypeAttributes<Double[]> attributes, Object instance, TypeSerializer serializer) throws IllegalAccessException {
            return new DoubleArrayValue(ValueData.of(attributes));
        }
    }
}
