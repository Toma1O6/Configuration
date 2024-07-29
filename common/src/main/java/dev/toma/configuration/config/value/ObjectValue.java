package dev.toma.configuration.config.value;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.validate.AggregatedValidationResult;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class ObjectValue extends ConfigValue<Map<String, ConfigValue<?>>> implements IHierarchical {

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
        format.writeMap(this.getId(), this.get(Mode.SAVED));
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
    public boolean isChangedFromDefault() {
        Map<String, ConfigValue<?>> map = this.get();
        for (ConfigValue<?> value : map.values()) {
            if (value.isChangedFromDefault()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void revertChanges() {
        super.revertChanges();
        this.get().values().forEach(ConfigValue::revertChanges);
    }

    @Override
    public void revertChangesToDefault() {
        super.revertChangesToDefault();
        this.get().values().forEach(ConfigValue::revertChangesToDefault);
    }

    @Override
    public void clearNetworkValues() {
        super.clearNetworkValues();
        for (ConfigValue<?> value : this.get().values()) {
            value.clearNetworkValues();
        }
    }

    @Override
    public AggregatedValidationResult getValidationResult() {
        AggregatedValidationResult result = super.getValidationResult();
        for (ConfigValue<?> value : this.get().values()) {
            AggregatedValidationResult valueResult = value.getValidationResult();
            if (valueResult != null && valueResult.severity().isWarningOrError()) {
                return AggregatedValidationResult.joinChild(result, valueResult);
            }
        }
        return result;
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        format.readMap(this.getId(), this.get().values());
    }

    @Override
    public <T> Optional<T> getChildValue(Iterator<String> iterator, Class<T> targetType) {
        return getChildValue(iterator, targetType, this.get());
    }

    @Override
    public <T> Optional<IConfigValue<T>> getChild(Iterator<String> iterator, Class<T> targetType) {
        return getChild(iterator, targetType, this.get());
    }

    @Override
    public IConfigValue<?> getChildById(String childId) {
        Map<String, ConfigValue<?>> map = this.get();
        return map.get(childId);
    }

    @Override
    public Collection<String> getChildrenKeys() {
        return this.get().keySet();
    }

    @Override
    public boolean shouldSynchronize() {
        return false;
    }

    @SuppressWarnings("unchecked")
    public static <V> Optional<IConfigValue<V>> getChild(Iterator<String> iterator, Class<V> targetType, Map<String, ConfigValue<?>> valueMap) {
        String key = iterator.next();
        ConfigValue<?> value = valueMap.get(key);
        if (!iterator.hasNext()) {
            if (targetType.isAssignableFrom(value.getValueType())) {
                return Optional.of((IConfigValue<V>) value);
            }
            Configuration.LOGGER.warn("Attempted to get invalid value definition {} in config!", key);
            return Optional.empty();
        } else if (value instanceof IHierarchical hierarchical) {
            return hierarchical.getChild(iterator, targetType);
        }
        Configuration.LOGGER.warn("Attempted to get non-existing value definition {} in config!", key);
        return Optional.empty();
    }

    public static <V> Optional<V> getChildValue(Iterator<String> iterator, Class<V> targetType, Map<String, ConfigValue<?>> valueMap) {
        String key = iterator.next();
        ConfigValue<?> value = valueMap.get(key);
        if (!iterator.hasNext()) {
            Object result = value.get();
            if (targetType.isAssignableFrom(value.getValueType())) {
                return Optional.of(targetType.cast(result));
            }
            Configuration.LOGGER.warn("Attempted to get invalid value {} in config!", key);
            return Optional.empty();
        } else if (value instanceof IHierarchical hierarchical) {
            return hierarchical.getChildValue(iterator, targetType);
        }
        Configuration.LOGGER.warn("Attempted to get non-existing value {} in config!", key);
        return Optional.empty();
    }

    @Override
    protected void readFieldData(Field field) {
        super.readFieldData(field);
        if (field.isAnnotationPresent(Configurable.Synchronized.class)) {
            Configuration.LOGGER.warn("Detected configurable object annotated with '@Configurable.Synchronized' annotation [{}.{}]. This has no effect and is most likely bug in this configuration. Contact the mod author", field.getDeclaringClass().getCanonicalName(), field.getName());
        }
    }

    public static final class Adapter extends TypeAdapter<Map<String, ConfigValue<?>>> {

        @Override
        public ConfigValue<Map<String, ConfigValue<?>>> serialize(TypeAttributes<Map<String, ConfigValue<?>>> attributes, Object instance, TypeSerializer serializer) throws IllegalAccessException {
            Class<?> type = instance.getClass();
            Map<String, ConfigValue<?>> map = serializer.serialize(type, instance);
            TypeAttributes<Map<String, ConfigValue<?>>> objectAttributes = attributes.child(attributes.id(), map, attributes.context());
            return new ObjectValue(ValueData.of(objectAttributes));
        }

        @Override
        public void encodeToBuffer(ConfigValue<Map<String, ConfigValue<?>>> value, FriendlyByteBuf buffer) {
            throw new UnsupportedOperationException("Object values cannot be serialized to network buffers!");
        }

        @Override
        public Map<String, ConfigValue<?>> decodeFromBuffer(ConfigValue<Map<String, ConfigValue<?>>> value, FriendlyByteBuf buffer) {
            throw new UnsupportedOperationException("Object values cannot be serialized to network buffers!");
        }

        @Override
        public void setFieldValue(Field field, Object instance, Object value) throws IllegalAccessException {
            // Do not set anything, keep existing instance
        }
    }
}
