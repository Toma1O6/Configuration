package dev.toma.configuration.config.value;

import dev.toma.configuration.config.ConfigUtils;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import net.minecraft.network.PacketBuffer;

import java.lang.reflect.Field;

public class DoubleValue extends DecimalValue<Double> {

    public DoubleValue(ValueData<Double> valueData) {
        super(valueData, Range.unboundedDouble());
    }

    @Override
    public Double getCorrectedValue(Double in) {
        if (this.range == null)
            return in;
        if (!this.range.isWithin(in)) {
            double corrected = this.range.clamp(in);
            ConfigUtils.logCorrectedMessage(this.getId(), in, corrected);
            return corrected;
        }
        return in;
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.writeDouble(this.getId(), this.get());
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.set(format.readDouble(this.getId()));
    }

    public static final class Adapter extends TypeAdapter {

        public Adapter() {
            super("double");
        }

        @Override
        public boolean isTargetType(Class<?> type) {
            return type.equals(Double.TYPE);
        }

        @Override
        public ConfigValue<?> serialize(String name, String[] comments, Object value, TypeSerializer serializer, AdapterContext context) throws IllegalAccessException {
            return new DoubleValue(ValueData.of(name, (double) value, context, comments));
        }

        @Override
        public void encodeToBuffer(ConfigValue<?> value, PacketBuffer buffer) {
            buffer.writeDouble((Double) value.get());
        }

        @Override
        public Object decodeFromBuffer(ConfigValue<?> value, PacketBuffer buffer) {
            return buffer.readDouble();
        }

        @Override
        public void setFieldValue(Field field, Object instance, Object value) throws IllegalAccessException {
            field.setDouble(instance, (Double) value);
        }
    }
}