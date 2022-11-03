package dev.toma.configuration.config.value;

import dev.toma.configuration.config.ConfigUtils;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.NumberDisplayType;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import net.minecraft.network.PacketBuffer;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;

public class FloatArrayValue extends ConfigValue<float[]> implements ArrayValue {

    private boolean fixedSize;
    private DecimalValue.Range range;
    private DecimalFormat format;
    private NumberDisplayType displayType;

    public FloatArrayValue(ValueData<float[]> valueData) {
        super(valueData);
    }

    @Override
    public boolean isFixedSize() {
        return fixedSize;
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
    protected float[] getCorrectedValue(float[] in) {
        if (this.fixedSize) {
            float[] defaultArray = this.valueData.getDefaultValue();
            if (in.length != defaultArray.length) {
                ConfigUtils.logArraySizeCorrectedMessage(this.getId(), Arrays.toString(in), Arrays.toString(defaultArray));
                in = defaultArray;
            }
        }
        if (this.range == null)
            return in;
        for (int i = 0; i < in.length; i++) {
            float value = in[i];
            if (!this.range.isWithin(value)) {
                float corrected = this.range.clamp(value);
                ConfigUtils.logCorrectedMessage(this.getId() + "[" + i + "]", value, corrected);
                in[i] = corrected;
            }
        }
        return in;
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.writeFloatArray(this.getId(), this.get());
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.set(format.readFloatArray(this.getId()));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        float[] floats = this.get();
        for (int i = 0; i < floats.length; i++) {
            builder.append(this.elementToString(floats[i]));
            if (i < floats.length - 1) {
                builder.append(",");
            }
        }
        builder.append("]");
        return builder.toString();
    }

    public static final class Adapter extends TypeAdapter {

        public Adapter() {
            super("float[]");
        }

        @Override
        public boolean isTargetType(Class<?> type) {
            return type.equals(float[].class);
        }

        @Override
        public void encodeToBuffer(ConfigValue<?> value, PacketBuffer buffer) {
            float[] arr = (float[]) value.get();
            buffer.writeInt(arr.length);
            for (float v : arr) {
                buffer.writeFloat(v);
            }
        }

        @Override
        public Object decodeFromBuffer(ConfigValue<?> value, PacketBuffer buffer) {
            float[] arr = new float[buffer.readInt()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = buffer.readFloat();
            }
            return arr;
        }

        @Override
        public ConfigValue<?> serialize(String name, String[] comments, Object value, TypeSerializer serializer, AdapterContext context) throws IllegalAccessException {
            return new FloatArrayValue(ValueData.of(name, (float[]) value, context, comments));
        }
    }
}
