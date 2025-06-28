package dev.toma.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.ConfigValueLocation;
import dev.toma.configuration.config.format.ConfigFormats;
import dev.toma.configuration.config.format.IConfigFormatHandler;
import dev.toma.configuration.config.io.ConfigurationFileManager;
import dev.toma.configuration.config.value.IConfigValue;
import dev.toma.configuration.service.ServiceHelper;
import dev.toma.configuration.service.services.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

/**
 * Main API entrypoint. Used for config registration
 *
 * @since 2.0
 * @author Toma
 */
public final class Configuration {

    public static final String MODID = "configuration";

    @ApiStatus.Internal
    public static final Logger LOGGER = LogManager.getLogger("Configuration");
    @ApiStatus.Internal
    public static final Platform PLATFORM = ServiceHelper.loadService(Platform.class);

    @ApiStatus.Internal
    public static ConfigHolder<ConfigurationOptions> options;

    @ApiStatus.Internal
    public static void setup() {
        if (PLATFORM.isDevelopmentEnvironment()) {
            registerSimpleJsonConfig(TestingConfig.class);
        }
        options = registerIniConfig(ConfigurationOptions.class);
    }

    /**
     * Codec for obtaining config holder by config ID. Could be useful for datapack config value reading for example.
     * @since 3.0
     */
    public static final Codec<ConfigHolder<?>> BY_ID_CODEC = Codec.STRING.comapFlatMap(
            id -> {
                Optional<ConfigHolder<Object>> optional = getConfig(id);
                return optional.map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "Unknown config ID '" + id + "'"));
            },
            ConfigHolder::getConfigId
    );

    /**
     * Registers your config class. Config will be immediately loaded upon calling.
     *
     * @param cfgClass Your config class
     * @param formatFactory File format to be used by this config class. You can use values
     *                      from {@link dev.toma.configuration.config.format.ConfigFormats} for example.
     * @return Config holder containing your config instance. You obtain it by calling
     * {@link ConfigHolder#getConfigInstance()} method.
     * @param <CFG> Config type
     * @since 2.0
     */
    public static <CFG> ConfigHolder<CFG> registerConfig(Class<CFG> cfgClass, IConfigFormatHandler formatFactory) {
        Config cfg = cfgClass.getAnnotation(Config.class);
        if (cfg == null) {
            throw new IllegalArgumentException("Config class must be annotated with '@Config' annotation");
        }
        String id = cfg.id();
        String filename = cfg.filename();
        if (filename.isEmpty()) {
            filename = id;
        }
        String group = cfg.group();
        if (group.isEmpty()) {
            group = id;
        }
        ConfigHolder<CFG> holder = new ConfigHolder<>(cfgClass, id, filename, group, formatFactory);
        ConfigHolder.registerConfig(holder);
        if (cfgClass.getAnnotation(Config.NoAutoSync.class) == null) {
            ConfigurationFileManager.FILE_WATCH_MANAGER.addTrackedConfig(holder);
        }
        return holder;
    }

    /**
     * Registers config class and returns the config instance itself rather than {@link ConfigHolder} as is the case
     * with {@link Configuration#registerConfig(Class, IConfigFormatHandler)}.
     *
     * @param cfgClass Your config class
     * @param formatFactory File format to be used by this config class. You can use values
     *                      from {@link dev.toma.configuration.config.format.ConfigFormats} for example.
     * @return Config instance for given class
     * @param <CFG> Config type
     * @since 4.0
     */
    public static <CFG> CFG registerSimpleConfig(Class<CFG> cfgClass, IConfigFormatHandler formatFactory) {
        return registerConfig(cfgClass, formatFactory).getConfigInstance();
    }

    /**
     * Registers config with JSON format.
     * @param cfgClass Your config class
     * @return {@link ConfigHolder} containing your config instance
     * @param <CFG> Config type
     * @since 4.0
     */
    public static <CFG> ConfigHolder<CFG> registerJsonConfig(Class<CFG> cfgClass) {
        return registerConfig(cfgClass, ConfigFormats.JSON);
    }

    /**
     * Registers config with JSON format.
     * @param cfgClass Your config class
     * @return Config instance for given class
     * @param <CFG> Config type
     * @since 4.0
     */
    public static <CFG> CFG registerSimpleJsonConfig(Class<CFG> cfgClass) {
        return registerJsonConfig(cfgClass).getConfigInstance();
    }

    /**
     * Registers config with Properties format.
     * @param cfgClass Your config class
     * @return {@link ConfigHolder} containing your config instance
     * @param <CFG> Config type
     * @since 4.0
     */
    public static <CFG> ConfigHolder<CFG> registerPropertiesConfig(Class<CFG> cfgClass) {
        return registerConfig(cfgClass, ConfigFormats.PROPERTIES);
    }

    /**
     * Registers config with Properties format.
     * @param cfgClass Your config class
     * @return Config instance for given class
     * @param <CFG> Config type
     * @since 4.0
     */
    public static <CFG> CFG registerSimplePropertiesConfig(Class<CFG> cfgClass) {
        return registerPropertiesConfig(cfgClass).getConfigInstance();
    }

    /**
     * Registers config with YAML format.
     * @param cfgClass Your config class
     * @return {@link ConfigHolder} containing your config instance
     * @param <CFG> Config type
     * @since 4.0
     */
    public static <CFG> ConfigHolder<CFG> registerYmlConfig(Class<CFG> cfgClass) {
        return registerConfig(cfgClass, ConfigFormats.YML);
    }

    /**
     * Registers config with YAML format.
     * @param cfgClass Your config class
     * @return Config instance for given class
     * @param <CFG> Config type
     * @since 4.0
     */
    public static <CFG> CFG registerSimpleYmlConfig(Class<CFG> cfgClass) {
        return registerYmlConfig(cfgClass).getConfigInstance();
    }

    /**
     * Registers config with INI format.
     * @param cfgClass Your config class
     * @return {@link ConfigHolder} containing your config instance
     * @param <CFG> Config type
     * @since 4.0
     */
    public static <CFG> ConfigHolder<CFG> registerIniConfig(Class<CFG> cfgClass) {
        return registerConfig(cfgClass, ConfigFormats.INI);
    }

    /**
     * Registers config with INI format.
     * @param cfgClass Your config class
     * @return Config instance for given class
     * @param <CFG> Config type
     * @since 4.0
     */
    public static <CFG> CFG registerSimpleIniConfig(Class<CFG> cfgClass) {
        return registerIniConfig(cfgClass).getConfigInstance();
    }

    /**
     * Allows you to get your config holder based on ID
     * @param id Config ID
     * @return Optional with config holder when such object exists
     * @param <CFG> Config type
     * @since 2.3.0
     */
    public static <CFG> Optional<ConfigHolder<CFG>> getConfig(String id) {
        return ConfigHolder.getConfig(id);
    }


    /**
     * Allows you to obtain config value for specific key within your config. For example when you have the following config
     * structure with integer value on path {@code config:numbers/myNumber}, and you want to obtain its value wrapper,
     * you can use this method with path parameter set to {@code Configuration.getConfigValueHolder("config:numbers/myNumber", Integer.class)}
     * to obtain the value wrapper. <br>
     * Unlike the {@link Configuration#getConfigValue(ConfigValueLocation, Class)} method, array index access will return the entire array wrapper.
     * This is because internally arrays do not hold config values for each array element. So you will have to use the config value to access
     * elements manually. This means the {@code type} attribute has to be {@code ARRAY}!<br>
     *
     * @param location {@link ConfigValueLocation} with namespace being the configId and path being the path to value within the config itself. Nested paths need to be separated by / characters
     * @param type Expected data type of the value
     * @return Optional with the specified value or {@link Optional#empty()} when the value does not exist or has different data type
     *
     * @since 4.0
     */
    public static <V> Optional<IConfigValue<V>> getConfigValueHolder(ConfigValueLocation location, Class<V> type) {
        ConfigHolder<?> holder = getConfig(location.namespace()).orElse(null);
        if (holder == null) {
            return Optional.empty();
        }
        return holder.getConfigValue(location.path(), type);
    }

    /**
     * Allows you to obtain value for specific key within config. For example when you have the following config
     * structure with integer value on path {@code config:numbers/myNumber}, and you want to obtain its value using key for any
     * reason (for example in json data definitions), you can use this method with path parameter set
     * to {@code Configuration.getValue("config:numbers/myNumber", Integer.class)} to obtain the value. <br>
     * The path can be also used for array values, for example when you want to get 3rd element in array, specify the path with array
     * index {@code config:numbers/numberArray/2} <br>
     *
     * @param location {@link ConfigValueLocation} with namespace being the configId and path being the path to value within the config itself. Nested paths need to be separated by / characters
     * @param type Expected data type of the value
     * @return Optional with the specified value or {@link Optional#empty()} when the value does not exist or has different data type
     *
     * @since 4.0
     */
    public static <V> Optional<V> getConfigValue(ConfigValueLocation location, Class<V> type) {
        ConfigHolder<?> holder = getConfig(location.namespace()).orElse(null);
        if (holder == null) {
            return Optional.empty();
        }
        return holder.getValue(location.path(), type);
    }
}
