package dev.toma.configuration.config.value;

import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import net.minecraft.network.FriendlyByteBuf;

public class BooleanArrayValue extends AbstractArrayValue<Boolean> {

    public BooleanArrayValue(ValueData<Boolean[]> valueData) {
        super(valueData);
    }

    @Override
    public Boolean createElementInstance() {
        return false;
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.writeBoolArray(this.getId(), this.get(Mode.SAVED));
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.setValue(format.readBoolArray(this.getId()));
    }

    public static final class Adapter extends TypeAdapter<Boolean[]> {

        @Override
        public ConfigValue<Boolean[]> serialize(TypeAttributes<Boolean[]> attributes, Object instance, TypeSerializer serializer) throws IllegalAccessException {
            return new BooleanArrayValue(ValueData.of(attributes));
        }

        @Override
        public void encodeToBuffer(ConfigValue<Boolean[]> value, FriendlyByteBuf buffer) {
            Boolean[] arr = value.get();
            buffer.writeInt(arr.length);
            for (Boolean b : arr) {
                buffer.writeBoolean(b);
            }
        }

        @Override
        public Boolean[] decodeFromBuffer(ConfigValue<Boolean[]> value, FriendlyByteBuf buffer) {
            Boolean[] arr = new Boolean[buffer.readInt()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = buffer.readBoolean();
            }
            return arr;
        }
    }
}
