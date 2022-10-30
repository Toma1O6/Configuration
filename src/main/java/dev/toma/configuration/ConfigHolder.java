package dev.toma.configuration;

import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.adapter.TypeAdapters;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.format.IConfigFormatHandler;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.io.ConfigIO;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public final class ConfigHolder<CFG> {

    private static final Map<String, ConfigHolder<?>> REGISTERED_CONFIGS = new HashMap<>();
    private final String configId;
    private final String filename;
    private final CFG configInstance;
    private final Class<CFG> configClass;
    private final IConfigFormatHandler format;
    private final Map<String, ConfigValue<?>> valueMap = new LinkedHashMap<>();

    ConfigHolder(Class<CFG> cfgClass, String configId, String filename, IConfigFormatHandler format) {
        this.configClass = cfgClass;
        this.configId = configId;
        this.filename = filename;
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
    }

    public String getConfigId() {
        return configId;
    }

    public String getFilename() {
        return filename;
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

    private Map<String, ConfigValue<?>> serializeType(Class<?> type, Object instance, boolean saveValue) throws IllegalAccessException {
        Map<String, ConfigValue<?>> map = new LinkedHashMap<>();
        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            Config.Value value = field.getAnnotation(Config.Value.class);
            if (value == null)
                continue;
            TypeAdapter adapter = TypeAdapters.getTypeAdapter(field.getType());
            if (adapter == null) {
                Configuration.LOGGER.warn(ConfigIO.MARKER, "Missing adapter for type {}, skipping serialization", field.getType());
                continue;
            }
            String[] comments = new String[0];
            Config.Comment comment = field.getAnnotation(Config.Comment.class);
            if (comment != null) {
                comments = comment.value();
            }
            ConfigValue<?> cfgValue = adapter.serialize(field.getName(), comments, field.get(instance), (type1, instance1) -> serializeType(type1, instance1, false), val -> {
                field.setAccessible(true);
                try {
                    adapter.setFieldValue(field, instance, val);
                } catch (IllegalAccessException e) {
                    Configuration.LOGGER.error(ConfigIO.MARKER, "Failed to update config value for field {} from {} to a new value {} due to error {}", field.getName(), type, val, e);
                }
            });
            map.put(field.getName(), cfgValue);
            if (saveValue) {
                this.assignValue(cfgValue);
            }
        }
        return map;
    }

    private <T> void assignValue(ConfigValue<T> value) {
        this.valueMap.put(value.getId(), value);
    }

    static void registerConfig(ConfigHolder<?> holder) {
        REGISTERED_CONFIGS.put(holder.configId, holder);
        ConfigIO.processConfig(holder);
    }

    @SuppressWarnings("unchecked")
    public static <CFG> Optional<ConfigHolder<CFG>> getConfig(String id) {
        ConfigHolder<CFG> value = (ConfigHolder<CFG>) REGISTERED_CONFIGS.get(id);
        return value == null ? Optional.empty() : Optional.of(value);
    }
}
