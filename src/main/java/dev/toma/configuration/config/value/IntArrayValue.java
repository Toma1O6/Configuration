package dev.toma.configuration.config.value;

import dev.toma.configuration.config.ConfigUtils;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import net.minecraft.network.PacketBuffer;

import java.lang.reflect.Field;
import java.util.Arrays;

public class IntArrayValue extends ConfigValue<int[]> implements ArrayValue {

    private boolean fixedSize;

    public IntArrayValue(ValueData<int[]> valueData) {
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
    protected int[] getCorrectedValue(int[] in) {
        if (this.fixedSize) {
            int[] defaultArray = this.valueData.getDefaultValue();
            if (in.length != defaultArray.length) {
                ConfigUtils.logArraySizeCorrectedMessage(this.getId(), Arrays.toString(in), Arrays.toString(defaultArray));
                return defaultArray;
            }
        }
        return in;
    }

    @Override
    public void serialize(IConfigFormat format) {
        format.writeIntArray(this.getId(), this.get());
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.set(format.readIntArray(this.getId()));
    }

    public static final class Adapter extends TypeAdapter {

        public Adapter() {
            super("int[]");
        }

        @Override
        public boolean isTargetType(Class<?> type) {
            return type.equals(int[].class);
        }

        @Override
        public void encodeToBuffer(ConfigValue<?> value, PacketBuffer buffer) {
            int[] arr = (int[]) value.get();
            buffer.writeInt(arr.length);
            for (int v : arr) {
                buffer.writeInt(v);
            }
        }

        @Override
        public Object decodeFromBuffer(ConfigValue<?> value, PacketBuffer buffer) {
            int[] arr = new int[buffer.readInt()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = buffer.readInt();
            }
            return arr;
        }

        @Override
        public ConfigValue<?> serialize(String name, String[] comments, Object value, TypeSerializer serializer, AdapterContext context) throws IllegalAccessException {
            return new IntArrayValue(ValueData.of(name, (int[]) value, context, comments));
        }
    }
}
