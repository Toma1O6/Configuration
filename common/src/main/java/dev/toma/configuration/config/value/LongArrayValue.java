package dev.toma.configuration.config.value;

import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.validate.NumberRange;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Field;

public class LongArrayValue extends NumericArrayValue<Long> {

    public LongArrayValue(ValueData<Long[]> valueData) {
        super(valueData, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    @Override
    public NumberRange<Long> getValueRange(Field field) {
        Configurable.Range range = field.getAnnotation(Configurable.Range.class);
        return range != null
                ? NumberRange.interval(this, range.min(), range.max())
                : NumberRange.all(this);
    }

    @Override
    public Long createElementInstance() {
        return 0L;
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.writeLongArray(this.getId(), this.get(Mode.SAVED));
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.setValue(format.readLongArray(this.getId()));
    }

    public static final class Adapter extends TypeAdapter<Long[]> {

        @Override
        public void encodeToBuffer(ConfigValue<Long[]> value, FriendlyByteBuf buffer) {
            saveToBuffer(value.get(), buffer, FriendlyByteBuf::writeLong);
        }

        @Override
        public Long[] decodeFromBuffer(ConfigValue<Long[]> value, FriendlyByteBuf buffer) {
            return readFromBuffer(buffer, Long[]::new, FriendlyByteBuf::readLong);
        }

        @Override
        public ConfigValue<Long[]> serialize(TypeAttributes<Long[]> attributes, Object instance, TypeSerializer serializer) throws IllegalAccessException {
            return new LongArrayValue(ValueData.of(attributes));
        }
    }
}
