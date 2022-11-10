package dev.toma.configuration.config;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.client.IValidationHandler;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.adapter.TypeAdapters;
import dev.toma.configuration.config.format.IConfigFormatHandler;
import dev.toma.configuration.config.io.ConfigIO;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.ObjectValue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public final class ConfigHolder<CFG> {

    private static final Map<String, ConfigHolder<?>> REGISTERED_CONFIGS = new HashMap<>();
    private final String configId;
    private final String filename;
    private final String group;
    private final CFG configInstance;
    private final Class<CFG> configClass;
    private final IConfigFormatHandler format;
    private final Map<String, ConfigValue<?>> valueMap = new LinkedHashMap<>();
    private final Map<String, ConfigValue<?>> networkSerializedFields = new HashMap<>();
    private final Set<IFileRefreshListener<CFG>> fileRefreshListeners = new HashSet<>();
    private final Object lock = new Object();

    public ConfigHolder(Class<CFG> cfgClass, String configId, String filename, String group, IConfigFormatHandler format) {
        this.configClass = cfgClass;
        this.configId = configId;
        this.filename = filename;
        this.group = group;
        try {
            this.configInstance = cfgClass.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
            Configuration.LOGGER.fatal(Configuration.MAIN_MARKER, "Failed to instantiate config class for {} config", configId);
            throw new RuntimeException("Config create failed", e);
        }
        try {
            serializeType(configClass, configInstance, true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Config serialize failed", e);
        }
        this.format = format;
        this.loadNetworkFields(valueMap, networkSerializedFields);
    }

    public void addFileRefreshListener(IFileRefreshListener<CFG> listener) {
        this.fileRefreshListeners.add(Objects.requireNonNull(listener));
    }

    public String getConfigId() {
        return configId;
    }

    public String getFilename() {
        return filename;
    }

    public String getGroup() {
        return group;
    }

    public CFG getConfigInstance() {
        return configInstance;
    }

    public Class<CFG> getConfigClass() {
        return configClass;
    }

    public IConfigFormatHandler getFormat() {
        return format;
    }

    public Collection<ConfigValue<?>> values() {
        return this.valueMap.values();
    }

    public Map<String, ConfigValue<?>> getValueMap() {
        return valueMap;
    }

    public Map<String, ConfigValue<?>> getNetworkSerializedFields() {
        return networkSerializedFields;
    }

    public void dispatchFileRefreshEvent() {
        this.fileRefreshListeners.forEach(listener -> listener.onFileRefresh(this));
    }

    public Object getLock() {
        return lock;
    }

    private Map<String, ConfigValue<?>> serializeType(Class<?> type, Object instance, boolean saveValue) throws IllegalAccessException {
        Map<String, ConfigValue<?>> map = new LinkedHashMap<>();
        Field[] fields = type.getFields();
        for (Field field : fields) {
            Configurable value = field.getAnnotation(Configurable.class);
            if (value == null)
                continue;
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                Configuration.LOGGER.warn(ConfigIO.MARKER, "Skipping config field {}, only instance non-final types are supported", field);
                continue;
            }
            TypeAdapter adapter = TypeAdapters.forType(field.getType());
            if (adapter == null) {
                Configuration.LOGGER.warn(ConfigIO.MARKER, "Missing adapter for type {}, skipping serialization", field.getType());
                continue;
            }
            String[] comments = new String[0];
            Configurable.Comment comment = field.getAnnotation(Configurable.Comment.class);
            if (comment != null) {
                comments = comment.value();
            }
            field.setAccessible(true);
            ConfigValue<?> cfgValue = adapter.serialize(field.getName(), comments, field.get(instance), (type1, instance1) -> serializeType(type1, instance1, false), new TypeAdapter.AdapterContext() {
                @Override
                public TypeAdapter getAdapter() {
                    return adapter;
                }

                @Override
                public Field getOwner() {
                    return field;
                }

                @Override
                public void setFieldValue(Object value) {
                    field.setAccessible(true);
                    try {
                        adapter.setFieldValue(field, instance, value);
                    } catch (IllegalAccessException e) {
                        Configuration.LOGGER.error(ConfigIO.MARKER, "Failed to update config value for field {} from {} to a new value {} due to error {}", field.getName(), type, value, e);
                    }
                }
            });
            Configurable.ChangeCallback callback = field.getAnnotation(Configurable.ChangeCallback.class);
            if (callback != null) {
                this.processCallback(callback, type, instance, cfgValue);
            }
            cfgValue.processFieldData(field);
            map.put(field.getName(), cfgValue);
            if (saveValue) {
                this.assignValue(cfgValue);
            }
        }
        return map;
    }

    private <T> void processCallback(Configurable.ChangeCallback callback, Class<?> type, Object instance, ConfigValue<T> value) {
        String methodName = callback.method();
        try {
            Class<?> valueType = value.getValueType();
            if (callback.allowPrimitivesMapping()) {
                valueType = ConfigUtils.remapPrimitiveType(valueType);
            }
            Method method = type.getDeclaredMethod(methodName, valueType, IValidationHandler.class);
            ConfigValue.SetValueCallback<T> setValueCallback = (val, handler) -> {
                try {
                    method.setAccessible(true);
                    method.invoke(instance, val, handler);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    Configuration.LOGGER.error(ConfigIO.MARKER, "Error occurred while invoking {} method: {}", method, e);
                }
            };
            value.setValueValidator(setValueCallback);
            Configuration.LOGGER.debug(ConfigIO.MARKER, "Attached new value listener method '{}' for config value {}", methodName, value.getId());
        } catch (NoSuchMethodException e) {
            Configuration.LOGGER.error(ConfigIO.MARKER, "Unable to map method {} for config value {} due to {}", methodName, value.getId(), e);
        } catch (Exception e) {
            Configuration.LOGGER.fatal(ConfigIO.MARKER, "Fatal error occurred while trying to map value listener for {} method", methodName);
            throw new RuntimeException("Value listener map failed", e);
        }
    }

    private <T> void assignValue(ConfigValue<T> value) {
        this.valueMap.put(value.getId(), value);
    }

    public static void registerConfig(ConfigHolder<?> holder) {
        REGISTERED_CONFIGS.put(holder.configId, holder);
        ConfigIO.processConfig(holder);
    }

    @SuppressWarnings("unchecked")
    public static <CFG> Optional<ConfigHolder<CFG>> getConfig(String id) {
        ConfigHolder<CFG> value = (ConfigHolder<CFG>) REGISTERED_CONFIGS.get(id);
        return value == null ? Optional.empty() : Optional.of(value);
    }

    public static Map<String, List<ConfigHolder<?>>> getConfigGroupingByGroup() {
        return REGISTERED_CONFIGS.values().stream().collect(Collectors.groupingBy(ConfigHolder::getGroup));
    }

    public static List<ConfigHolder<?>> getConfigsByGroup(String group) {
        return REGISTERED_CONFIGS.values().stream()
                .filter(configHolder -> configHolder.group.equals(group))
                .collect(Collectors.toList());
    }

    public static Set<String> getSynchronizedConfigs() {
        return REGISTERED_CONFIGS.entrySet()
                .stream()
                .filter(e -> e.getValue().networkSerializedFields.size() > 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    private void loadNetworkFields(Map<String, ConfigValue<?>> src, Map<String, ConfigValue<?>> dest) {
        src.values().forEach(value -> {
            if (value instanceof ObjectValue) {
                Map<String, ConfigValue<?>> data = ((ObjectValue) value).get();
                loadNetworkFields(data, dest);
            } else {
                if (!value.shouldSynchronize())
                    return;
                String path = value.getFieldPath();
                dest.put(path, value);
            }
        });
    }

    @FunctionalInterface
    public interface IFileRefreshListener<CFG> {
        void onFileRefresh(ConfigHolder<CFG> holder);
    }
}
