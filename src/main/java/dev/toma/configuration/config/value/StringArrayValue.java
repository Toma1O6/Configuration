package dev.toma.configuration.config.value;

import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import net.minecraft.network.PacketBuffer;

public class StringArrayValue extends ConfigValue<String[]> {

    public StringArrayValue(ValueData<String[]> valueData) {
        super(valueData);
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.writeStringArray(this.getId(), this.get());
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.set(format.readStringArray(this.getId()));
    }

    public static final class Adapter extends TypeAdapter {

        public Adapter() {
            super("string[]");
        }

        @Override
        public boolean isTargetType(Class<?> type) {
            return type.equals(String[].class);
        }

        @Override
        public void encodeToBuffer(ConfigValue<?> value, PacketBuffer buffer) {
            String[] arr = (String[]) value.get();
            buffer.writeInt(arr.length);
            for (String v : arr) {
                buffer.writeUtf(v);
            }
        }

        @Override
        public Object decodeFromBuffer(ConfigValue<?> value, PacketBuffer buffer) {
            String[] arr = new String[buffer.readInt()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = buffer.readUtf();
            }
            return arr;
        }

        @Override
        public ConfigValue<?> serialize(String name, String[] comments, Object value, TypeSerializer serializer, AdapterContext context) throws IllegalAccessException {
            return new StringArrayValue(ValueData.of(name, (String[]) value, context, comments));
        }
    }
}
