package dev.toma.configuration.config.value;

import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import net.minecraft.network.PacketBuffer;

public class FloatArrayValue extends ConfigValue<float[]> {

    public FloatArrayValue(ValueData<float[]> valueData) {
        super(valueData);
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.writeFloatArray(this.getId(), this.get());
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.set(format.readFloatArray(this.getId()));
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
