package dev.toma.configuration.config.value;

import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import net.minecraft.network.PacketBuffer;

public class BooleanArrayValue extends ConfigValue<boolean[]> {

    public BooleanArrayValue(ValueData<boolean[]> valueData) {
        super(valueData);
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
