package dev.toma.configuration.config.value;

import dev.toma.configuration.config.ConfigUtils;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import net.minecraft.network.PacketBuffer;

import java.lang.reflect.Field;
import java.util.Arrays;

public class LongArrayValue extends ConfigValue<long[]> {

    private boolean fixedSize;

    public LongArrayValue(ValueData<long[]> valueData) {
        super(valueData);
    }

    @Override
    protected void readFieldData(Field field) {
        this.fixedSize = field.getAnnotation(Configurable.FixedSize.class) != null;
    }

    @Override
    protected long[] getCorrectedValue(long[] in) {
        if (this.fixedSize) {
            long[] defaultArray = this.valueData.getDefaultValue();
            if (in.length != defaultArray.length) {
                ConfigUtils.logArraySizeCorrectedMessage(this.getId(), Arrays.toString(in), Arrays.toString(defaultArray));
                return defaultArray;
            }
        }
        return in;
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.writeLongArray(this.getId(), this.get());
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.set(format.readLongArray(this.getId()));
    }

    public static final class Adapter extends TypeAdapter {

        public Adapter() {
            super("long[]");
        }

        @Override
        public boolean isTargetType(Class<?> type) {
            return type.equals(long[].class);
        }

        @Override
        public void encodeToBuffer(ConfigValue<?> value, PacketBuffer buffer) {
            long[] arr = (long[]) value.get();
            buffer.writeInt(arr.length);
            for (long v : arr) {
                buffer.writeLong(v);
            }
        }

        @Override
        public Object decodeFromBuffer(ConfigValue<?> value, PacketBuffer buffer) {
            long[] arr = new long[buffer.readInt()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = buffer.readLong();
            }
            return arr;
        }

        @Override
        public ConfigValue<?> serialize(String name, String[] comments, Object value, TypeSerializer serializer, AdapterContext context) throws IllegalAccessException {
            return new LongArrayValue(ValueData.of(name, (long[]) value, context, comments));
        }
    }
}
