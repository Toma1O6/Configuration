package dev.toma.configuration.config.value;

import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.validate.NumberRange;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Field;

public class ByteArrayValue extends NumericArrayValue<Byte> {

    public ByteArrayValue(ValueData<Byte[]> valueData) {
        super(valueData, Byte.MIN_VALUE, Byte.MAX_VALUE);
    }

    @Override
    public NumberRange<Byte> getValueRange(Field field) {
        Configurable.Range range = field.getAnnotation(Configurable.Range.class);
        return range != null
                ? NumberRange.interval(this, (byte) Math.max(range.min(), min()), (byte) Math.min(range.max(), max()))
                : NumberRange.all(this);
    }

    @Override
    public Byte createElementInstance() {
        return 0;
    }

    @Override
    public void serialize(IConfigFormat format) {
        format.writeByteArray(this.getId(), this.get(Mode.SAVED));
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.setValue(format.readByteArray(this.getId()));
    }

    public static final class Adapter extends TypeAdapter<Byte[]> {

        @Override
        public void encodeToBuffer(ConfigValue<Byte[]> value, FriendlyByteBuf buffer) {
            saveToBuffer(value.get(), buffer, (buf, aByte) -> buf.writeByte(aByte));
        }

        @Override
        public Byte[] decodeFromBuffer(ConfigValue<Byte[]> value, FriendlyByteBuf buffer) {
            return readFromBuffer(buffer, Byte[]::new, FriendlyByteBuf::readByte);
        }

        @Override
        public ConfigValue<Byte[]> serialize(TypeAttributes<Byte[]> attributes, Object instance, TypeSerializer serializer) throws IllegalAccessException {
            return new ByteArrayValue(ValueData.of(attributes));
        }
    }
}
