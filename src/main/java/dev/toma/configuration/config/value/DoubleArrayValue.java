package dev.toma.configuration.config.value;

import dev.toma.configuration.config.ConfigUtils;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.NumberDisplayType;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;

public class DoubleArrayValue extends ConfigValue<double[]> implements NumericArrayValue {

    private boolean fixedSize;
    private DecimalValue.Range range;
    private DecimalFormat format;
    private NumberDisplayType displayType;

    public DoubleArrayValue(ValueData<double[]> valueData) {
        super(valueData);
    }

    @Override
    public boolean isFixedSize() {
        return fixedSize;
    }

    @Nullable
    @Override
    public DecimalFormat getDecimalFormat() {
        return format;
    }

    @Override
    public NumberDisplayType getDisplayType() {
        return displayType;
    }

    @Override
    protected void readFieldData(Field field) {
        this.fixedSize = field.getAnnotation(Configurable.FixedSize.class) != null;
        Configurable.DecimalRange decimalRange = field.getAnnotation(Configurable.DecimalRange.class);
        if (decimalRange != null) {
            this.range = DecimalValue.Range.newBoundedRange(decimalRange.min(), decimalRange.max());
        }
        Configurable.Gui.DecimalNumberFormat decimalNumberFormat = field.getAnnotation(Configurable.Gui.DecimalNumberFormat.class);
        if (decimalNumberFormat != null) {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator('.');
            this.format = new DecimalFormat(decimalNumberFormat.value(), symbols);
        }
        Configurable.Gui.NumberDisplay display = field.getAnnotation(Configurable.Gui.NumberDisplay.class);
        if (display != null) {
            this.displayType = display.value();
        }
    }

    @Override
    protected double[] getCorrectedValue(double[] in) {
        if (this.fixedSize) {
            double[] defaultArray = this.valueData.getDefaultValue();
            if (in.length != defaultArray.length) {
                ConfigUtils.logArraySizeCorrectedMessage(this.getId(), Arrays.toString(in), Arrays.toString(defaultArray));
                in = defaultArray;
            }
        }
        if (this.range == null)
            return in;
        for (int i = 0; i < in.length; i++) {
            double value = in[i];
            if (!this.range.isWithin(value)) {
                double corrected = this.range.clamp(value);
                ConfigUtils.logCorrectedMessage(this.getId() + "[" + i + "]", value, corrected);
                in[i] = corrected;
            }
        }
        return in;
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.writeDoubleArray(this.getId(), this.get());
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.set(format.readDoubleArray(this.getId()));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        double[] doubles = this.get();
        for (int i = 0; i < doubles.length; i++) {
            builder.append(this.elementToString(doubles[i]));
            if (i < doubles.length - 1) {
                builder.append(",");
            }
        }
        builder.append("]");
        return builder.toString();
    }

    public static final class Adapter extends TypeAdapter {

        public Adapter() {
            super("double[]");
        }

        @Override
        public boolean isTargetType(Class<?> type) {
            return type.equals(double[].class);
        }

        @Override
        public void encodeToBuffer(ConfigValue<?> value, PacketBuffer buffer) {
            double[] arr = (double[]) value.get();
            buffer.writeInt(arr.length);
            for (double v : arr) {
                buffer.writeDouble(v);
            }
        }

        @Override
        public Object decodeFromBuffer(ConfigValue<?> value, PacketBuffer buffer) {
            double[] arr = new double[buffer.readInt()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = buffer.readDouble();
            }
            return arr;
        }

        @Override
        public ConfigValue<?> serialize(String name, String[] comments, Object value, TypeSerializer serializer, AdapterContext context) throws IllegalAccessException {
            return new DoubleArrayValue(ValueData.of(name, (double[]) value, context, comments));
        }
    }
}
