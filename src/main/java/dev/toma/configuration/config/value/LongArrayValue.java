package dev.toma.configuration.config.value;

import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import net.minecraft.network.PacketBuffer;

public class LongArrayValue extends ConfigValue<long[]> {

    public LongArrayValue(ValueData<long[]> valueData) {
        super(valueData);
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
