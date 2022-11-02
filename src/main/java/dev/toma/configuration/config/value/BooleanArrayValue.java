package dev.toma.configuration.config.value;

import dev.toma.configuration.config.ConfigUtils;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import net.minecraft.network.PacketBuffer;

import java.lang.reflect.Field;
import java.util.Arrays;

public class BooleanArrayValue extends ConfigValue<boolean[]> implements ArrayValue {

    private boolean fixedSize;

    public BooleanArrayValue(ValueData<boolean[]> valueData) {
        super(valueData);
    }

    @Override
    public boolean isFixedSize() {
        return fixedSize;
    }

    @Override
    protected void readFieldData(Field field) {
        this.fixedSize = field.getAnnotation(Configurable.FixedSize.class) != null;
    }

    @Override
    protected boolean[] getCorrectedValue(boolean[] in) {
        if (this.fixedSize) {
            boolean[] defaultArray = this.valueData.getDefaultValue();
            if (in.length != defaultArray.length) {
                ConfigUtils.logArraySizeCorrectedMessage(this.getId(), Arrays.toString(in), Arrays.toString(defaultArray));
                return defaultArray;
            }
        }
        return in;
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.writeBoolArray(this.getId(), this.get());
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.set(format.readBoolArray(this.getId()));
    }

    public static final class Adapter extends TypeAdapter {

        public Adapter() {
            super("boolean[]");
        }

        @Override
        public boolean isTargetType(Class<?> type) {
            return type.equals(boolean[].class);
        }

        @Override
        public ConfigValue<?> serialize(String name, String[] comments, Object value, TypeSerializer serializer, AdapterContext context) throws IllegalAccessException {
            return new BooleanArrayValue(ValueData.of(name, (boolean[]) value, context, comments));
        }

        @Override
        public void encodeToBuffer(ConfigValue<?> value, PacketBuffer buffer) {
            boolean[] arr = (boolean[]) value.get();
            buffer.writeInt(arr.length);
            for (boolean b : arr) {
                buffer.writeBoolean(b);
            }
        }

        @Override
        public Object decodeFromBuffer(ConfigValue<?> value, PacketBuffer buffer) {
            boolean[] arr = new boolean[buffer.readInt()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = buffer.readBoolean();
            }
            return arr;
        }
    }
}
