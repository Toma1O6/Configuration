package dev.toma.configuration.config.value;

import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Field;

public class LongArrayValue extends NumericArrayValue<Long> {

    public LongArrayValue(ValueData<Long[]> valueData) {
        super(valueData, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    @Override
    public ValueRange<Long> getValueRange(Field field, Long typeMin, Long typeMax) {
        Configurable.Range range = field.getAnnotation(Configurable.Range.class);
        return range != null
                ? new ValueRange<>(range.min(), range.max())
                : new ValueRange<>(typeMin, typeMax);
    }

    @Override
    public Long createElementInstance() {
        return 0L;
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.writeLongArray(this.getId(), this.get());
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.setValue(format.readLongArray(this.getId()));
    }

    public static final class Adapter extends TypeAdapter {

        @Override
        public void encodeToBuffer(ConfigValue<?> value, FriendlyByteBuf buffer) {
            saveToBuffer((Long[]) value.get(), buffer, FriendlyByteBuf::writeLong);
        }

        @Override
        public Object decodeFromBuffer(ConfigValue<?> value, FriendlyByteBuf buffer) {
            return readFromBuffer(buffer, Long[]::new, FriendlyByteBuf::readLong);
        }

        @Override
        public ConfigValue<?> serialize(String name, String[] comments, Object value, TypeSerializer serializer, AdapterContext context) throws IllegalAccessException {
            return new LongArrayValue(ValueData.of(name, (Long[]) value, context, comments));
        }
    }
}
