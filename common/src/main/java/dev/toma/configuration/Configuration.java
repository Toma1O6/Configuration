package dev.toma.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.format.ConfigFormats;
import dev.toma.configuration.config.format.IConfigFormatHandler;
import dev.toma.configuration.config.io.ConfigIO;
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
    public static void setup() {
        if (PLATFORM.isDevelopmentEnvironment()) {
            registerConfig(TestingConfig.class, ConfigFormats.YAML);
        }

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
            ConfigIO.FILE_WATCH_MANAGER.addTrackedConfig(holder);
        }
        return holder;
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
}
