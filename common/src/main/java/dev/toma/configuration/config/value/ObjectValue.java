package dev.toma.configuration.config.value;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class ObjectValue extends ConfigValue<Map<String, ConfigValue<?>>> implements HierarchicalConfigValue {

    public ObjectValue(ValueData<Map<String, ConfigValue<?>>> valueData) {
        super(valueData);
        this.get().values().forEach(value -> value.setParent(this));
    }

    @Override
    public void save() {
        for (ConfigValue<?> child : this.get().values()) {
            child.save();
        }
        super.save();
    }

    @Override
    public void serialize(IConfigFormat format) {
        format.writeMap(this.getId(), this.get());
    }

    @Override
    public boolean isChanged() {
        Map<String, ConfigValue<?>> map = this.get();
        for (ConfigValue<?> value : map.values()) {
            if (value.isChanged()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        format.readMap(this.getId(), this.get().values());
    }

    @Override
    public void setValueValidator(SetValueCallback<Map<String, ConfigValue<?>>> callback) {
        throw new UnsupportedOperationException("Cannot attach value validator to Object types!");
    }

    @Override
    public <T> Optional<T> getChild(Iterator<String> iterator, Class<T> targetType) {
        return getChildValue(iterator, targetType, this.get());
    }

    public static <V> Optional<V> getChildValue(Iterator<String> iterator, Class<V> targetType, Map<String, ConfigValue<?>> valueMap) {
        String key = iterator.next();
        ConfigValue<?> value = valueMap.get(key);
        if (!iterator.hasNext()) {
            Object result = value.get();
            if (targetType.isAssignableFrom(value.getValueType())) {
                return Optional.of(targetType.cast(result));
            }
            Configuration.LOGGER.warn("Attempted to get invalid value type {} in config!", key);
            return Optional.empty();
        } else if (value instanceof HierarchicalConfigValue hierarchicalConfigValue) {
            return hierarchicalConfigValue.getChild(iterator, targetType);
        }
        Configuration.LOGGER.warn("Attempted to get non-existing value {} in config!", key);
        return Optional.empty();
    }

    public static final class Adapter extends TypeAdapter {

        @Override
        public ConfigValue<?> serialize(String name, String[] comments, Object value, TypeSerializer serializer, AdapterContext context) throws IllegalAccessException {
            Class<?> type = value.getClass();
            Map<String, ConfigValue<?>> map = serializer.serialize(type, value);
            return new ObjectValue(ValueData.of(name, map, context, comments));
        }

        @Override
        public void encodeToBuffer(ConfigValue<?> value, FriendlyByteBuf buffer) {
        }

        @Override
        public Object decodeFromBuffer(ConfigValue<?> value, FriendlyByteBuf buffer) {
            return null;
        }

        @Override
        public void setFieldValue(Field field, Object instance, Object value) throws IllegalAccessException {
            // Do not set anything, keep existing instance
        }
    }
}
