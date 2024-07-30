package dev.toma.configuration.config;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.adapter.TypeAdapterManager;
import dev.toma.configuration.config.adapter.TypeAttributes;
import dev.toma.configuration.config.adapter.TypeMapper;
import dev.toma.configuration.config.format.IConfigFormatHandler;
import dev.toma.configuration.config.io.ConfigIO;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.IConfigValue;
import dev.toma.configuration.config.value.IConfigValueReadable;
import dev.toma.configuration.config.value.ObjectValue;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages config values and stores some default parameters of your config class.
 * This class also acts as config registry.
 *
 * @param <CFG> Your config type
 * @author Toma
 * @since 2.0
 */
public final class ConfigHolder<CFG> {

    // Map of all registered configs
    private static final Map<String, ConfigHolder<?>> REGISTERED_CONFIGS = new LinkedHashMap<>();
    // Unique config ID
    private final String configId;
    // Config filename without extension
    private final String filename;
    // Config group, same as config ID unless changed
    private final String group;
    // Registered config instance
    private final CFG configInstance;
    // Type of config
    private final Class<CFG> configClass;
    // File format used by this config
    private final IConfigFormatHandler format;
    // Mapping of all config values
    private final Map<String, ConfigValue<?>> valueMap = new LinkedHashMap<>();
    // Map of fields which will be synced to client upon login
    private final Map<String, ConfigValue<?>> networkSerializedFields = new LinkedHashMap<>();
    // Title component for GUI displays
    private final Component title;
    // Lock for async operations
    private final Object lock = new Object();

    public ConfigHolder(Class<CFG> cfgClass, String configId, String filename, String group, IConfigFormatHandler format) {
        this.configClass = cfgClass;
        this.configId = configId;
        this.filename = filename;
        this.group = group;
        try {
            this.configInstance = cfgClass.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
            Configuration.LOGGER.fatal("Failed to instantiate config class for {} config", configId);
            throw new RuntimeException("Config create failed", e);
        }
        try {
            serializeType(configClass, configInstance, true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Config serialize failed", e);
        }
        this.format = format;
        this.loadNetworkFields(valueMap, networkSerializedFields);
        this.title = Component.translatable("config.screen." + this.configId);
    }

    /**
     * Registers config to internal registry. You should never call
     * this method. Instead, use {@link Configuration#registerConfig(Class, IConfigFormatHandler)} for config registration
     * @param holder Config holder to be registered
     */
    @ApiStatus.Internal
    public static void registerConfig(ConfigHolder<?> holder) {
        REGISTERED_CONFIGS.put(holder.configId, holder);
        ConfigIO.processConfig(holder);
    }

    /**
     * Allows you to get your config holder based on ID
     * @param id Config ID
     * @return Optional with config holder when such object exists
     * @param <CFG> Config type
     */
    @ApiStatus.Internal
    @SuppressWarnings("unchecked")
    public static <CFG> Optional<ConfigHolder<CFG>> getConfig(String id) {
        ConfigHolder<CFG> value = (ConfigHolder<CFG>) REGISTERED_CONFIGS.get(id);
        return value == null ? Optional.empty() : Optional.of(value);
    }

    /**
     * Groups all configs from registry into Group-List
     * @return Mapped values
     */
    @ApiStatus.Internal
    public static Map<String, List<ConfigHolder<?>>> getConfigGroupingByGroup() {
        return REGISTERED_CONFIGS.values().stream().collect(Collectors.groupingBy(ConfigHolder::getGroup));
    }

    /**
     * Returns list of config holders for the specified group
     * @param group Group ID
     * @return List with config holders. May be empty.
     */
    @ApiStatus.Internal
    public static List<ConfigHolder<?>> getConfigsByGroup(String group) {
        return REGISTERED_CONFIGS.values().stream()
                .filter(configHolder -> configHolder.group.equals(group))
                .collect(Collectors.toList());
    }

    /**
     * Obtain all configs which have some network serialized values
     * @return Set of config holders which need to be synchronized to client
     */
    @ApiStatus.Internal
    public static Set<String> getSynchronizedConfigs() {
        return REGISTERED_CONFIGS.entrySet()
                .stream()
                .filter(e -> !e.getValue().networkSerializedFields.isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * @return Whether any config value does not match the in memory value
     */
    public boolean isChanged() {
        return this.values().stream().anyMatch(ConfigValue::isChanged);
    }

    /**
     * @return Whether any config value does not match the default value
     */
    public boolean isChangedFromDefault() {
        return this.values().stream().anyMatch(ConfigValue::isChangedFromDefault);
    }

    /**
     * Saves all pending values in value wrappers as long as their {@link dev.toma.configuration.config.Configurable.UpdateRestriction} allow it
     */
    public void save() {
        this.values().forEach(ConfigValue::save);
    }

    /**
     * Restores client saved values and clears network value cache on each value
     */
    public void restoreClientStoredValues() {
        this.values().forEach(ConfigValue::clearNetworkValues);
    }

    /**
     * Allows you to obtain value for specific key within your config. For example when you have the following config
     * structure with integer value on path {@code modid.numbers.myNumber}, and you want to obtain its value using key for any
     * reason (for example in json datasource definitions), you can use this method with path parameter set
     * to {@code myConfigHolder.getValue("modid.numbers.myNumber", Integer.class)} to obtain the value. <br>
     * The path can be also used for array values, for example when you want to get 3rd element in array, specify the path with array
     * index {@code modid.numbers.numberArray.2} <br>
     *
     * <b>Keep in mind that this method fails quietly with only warning being logged to console!</b>
     *
     * @param path The path to your variable in config
     * @param expectedType Expected data type of the value
     * @return Optional with the specified value or {@link Optional#empty()} when the value does not exist or has different data type
     *
     * @since 2.3.0
     */
    public <V> Optional<V> getValue(String path, Class<V> expectedType) {
        String[] keys = path.split("\\.");
        Iterator<String> stringIterator = Arrays.asList(keys).iterator();
        return ObjectValue.getChildValue(stringIterator, expectedType, valueMap);
    }

    /**
     * Allows you to obtain config value for specific key within your config. For example when you have the following config
     * structure with integer value on path {@code modid.numbers.myNumber}, and you want to obtain its value wrapper,
     * you can use this method with path parameter set to {@code myConfigHolder.getConfigValue("modid.numbers.myNumber", Integer.class)}
     * to obtain the value wrapper. <br>
     * Unlike the {@link ConfigHolder#getValue(String, Class)} method, array index access will return the entire array wrapper.
     * This is because internally arrays do not hold config values for each array element. So you will have to use the config value to access
     * elements manually. So the {@code expectedType} attribute has to be {@code ARRAY}!<br>
     *
     * <b>Keep in mind that this method fails quietly with only warning being logged to console!</b>
     *
     * @param path The path to your variable in config
     * @param expectedType Expected data type of the value
     * @return Optional with the specified value or {@link Optional#empty()} when the value does not exist or has different data type
     *
     * @since 3.0
     */
    public <V> Optional<IConfigValue<V>> getConfigValue(String path, Class<V> expectedType) {
        String[] keys = path.split("\\.");
        Iterator<String> stringIterator = Arrays.asList(keys).iterator();
        return ObjectValue.getChild(stringIterator, expectedType, valueMap);
    }

    /**
     * @return ID of this config
     */
    public String getConfigId() {
        return configId;
    }

    /**
     * @return Filename without extension for this config
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @return Group ID of this config
     */
    public String getGroup() {
        return group;
    }

    /**
     * @return Your registered config
     */
    public CFG getConfigInstance() {
        return configInstance;
    }

    /**
     * @return Type of config
     */
    public Class<CFG> getConfigClass() {
        return configClass;
    }

    /**
     * @return File format factory for this config
     */
    @ApiStatus.Internal
    public IConfigFormatHandler getFormat() {
        return format;
    }

    /**
     * @return Collection of mapped config values
     */
    @ApiStatus.Internal
    public Collection<ConfigValue<?>> values() {
        return this.valueMap.values();
    }

    /**
     * @return Map ID-ConfigValue for this config
     */
    @ApiStatus.Internal
    public Map<String, ConfigValue<?>> getValueMap() {
        return valueMap;
    }

    /**
     * @return Map ID-ConfigValue for network serialization
     */
    @ApiStatus.Internal
    public Map<String, ConfigValue<?>> getNetworkSerializedFields() {
        return networkSerializedFields;
    }

    /**
     * @return Localized component name of this config
     */
    public Component getTitle() {
        return title;
    }

    /**
     * @return Lock for async operations. Used for IO operations currently
     */
    @ApiStatus.Internal
    public Object getLock() {
        return lock;
    }

    @ApiStatus.Internal
    public static Collection<String> getRegisteredConfigs() {
        return REGISTERED_CONFIGS.keySet();
    }

    @SuppressWarnings("unchecked")
    private <T> Map<String, ConfigValue<?>> serializeType(Class<?> type, Object instance, boolean saveValue) throws IllegalAccessException {
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
            TypeAttributes<T> attributes = (TypeAttributes<T>) TypeAdapterManager.forType(field.getType());
            TypeAdapter<T> adapter = attributes.adapter();
            if (adapter == null) {
                Configuration.LOGGER.warn(ConfigIO.MARKER, "Missing adapter for type {}, skipping serialization", field.getType());
                continue;
            }
            String[] comments = new String[0];
            Configurable.Comment comment = field.getAnnotation(Configurable.Comment.class);
            boolean localizeComments = false;
            if (comment != null) {
                comments = comment.value();
                localizeComments = comment.localize();
            }
            Configurable.LocalizationKey localizationType = value.key();
            field.setAccessible(true);
            Object fieldValue = field.get(instance);
            TypeMapper<T, Object> mapper = attributes.mapper();
            Object migratedField = mapper.migrate((T) fieldValue);
            TypeAdapter.AdapterContext context = this.getAdapterContext(adapter, type, field, mapper, instance);
            TypeAdapter.TypeAttributes<T> typeAttributes = new TypeAdapter.TypeAttributes<>(this.configId, field.getName(), (T) migratedField, context, localizationType, comments, localizeComments);
            ConfigValue<?> cfgValue = adapter.serialize(typeAttributes, migratedField, (t, i) -> this.serializeType(t, i, false));
            cfgValue.processFieldData(field);
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

    private void loadNetworkFields(Map<String, ConfigValue<?>> src, Map<String, ConfigValue<?>> dest) {
        src.values().forEach(value -> {
            if (value instanceof ObjectValue objValue) {
                Map<String, ConfigValue<?>> data = objValue.get(IConfigValueReadable.Mode.SAVED);
                loadNetworkFields(data, dest);
            } else {
                if (!value.shouldSynchronize())
                    return;
                String path = value.getPath();
                dest.put(path, value);
            }
        });
    }

    private TypeAdapter.AdapterContext getAdapterContext(TypeAdapter<?> parent, Class<?> type, Field field, TypeMapper<?, Object> mapper, Object instance) {
        return new TypeAdapter.AdapterContext() {
            @Override
            public TypeAdapter<?> getAdapter() {
                return parent;
            }

            @Override
            public Field getOwner() {
                return field;
            }

            @Override
            public void setFieldValue(Object value) {
                field.setAccessible(true);
                try {
                    Object remapped = mapper.rollback(value);
                    parent.setFieldValue(field, instance, remapped);
                } catch (IllegalAccessException e) {
                    Configuration.LOGGER.error(ConfigIO.MARKER, "Failed to update config value for field {} from {} to a new value {} due to error {}", field.getName(), type, value, e);
                }
            }
        };
    }
}
