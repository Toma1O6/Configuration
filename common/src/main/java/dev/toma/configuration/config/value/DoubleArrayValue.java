package dev.toma.configuration.config.value;

import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Field;

public class DoubleArrayValue extends NumericArrayValue<Double> {

    public DoubleArrayValue(ValueData<Double[]> valueData) {
        super(valueData, -Double.MAX_VALUE, Double.MAX_VALUE);
    }

    @Override
    public ValueRange<Double> getValueRange(Field field, Double typeMin, Double typeMax) {
        Configurable.DecimalRange decimalRange = field.getAnnotation(Configurable.DecimalRange.class);
        return decimalRange != null
                ? new ValueRange<>(decimalRange.min(), decimalRange.max())
                : new ValueRange<>(typeMin, typeMax);
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
        public ConfigValue<?> serialize(String name, String[] comments, Object value, TypeSerializer serializer, AdapterContext context) throws IllegalAccessException {
            return new DoubleArrayValue(ValueData.of(name, (Double[]) value, context, comments));
        }
    }
}
