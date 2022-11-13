package dev.toma.configuration;

import dev.toma.configuration.client.screen.ConfigGroupScreen;
import dev.toma.configuration.client.screen.ConfigScreen;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.format.ConfigFormats;
import dev.toma.configuration.config.format.IConfigFormatHandler;
import dev.toma.configuration.config.io.ConfigIO;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.network.Networking;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.gui.screens.Screen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public final class Configuration implements ModInitializer {

    public static final String MODID = "configuration";
    public static final Logger LOGGER = LogManager.getLogger("Configuration");
    public static final Marker MAIN_MARKER = MarkerManager.getMarker("main");

    public Configuration() {
    }

    @Override
    public void onInitialize() {
        Networking.PacketRegistry.register();
        ConfigIO.FILE_WATCH_MANAGER.startService();
    }

    /**
     * Registers your config class. Config will be immediately loaded upon calling.
     *
     * @param cfgClass Your config class
     * @param formatFactory File format to be used by this config class. You can use values
     *                      from {@link ConfigFormats} for example.
     * @return Config holder containing your config instance. You obtain it by calling
     * {@link ConfigHolder#getConfigInstance()} method.
     * @param <CFG> Config type
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
     * You can obtain default config screen based on provided config class.
     *
     * @param configClass Your config class
     * @param previous Previously open screen
     * @return Either new config screen or {@code null} when no config exists for the provided class
     */
    @Nullable
    @Environment(EnvType.CLIENT)
    public static Screen getConfigScreen(Class<?> configClass, Screen previous) {
        Config cfg = configClass.getAnnotation(Config.class);
        if (cfg == null) {
            return null;
        }
        String id = cfg.id();
        return getConfigScreen(id, previous);
    }

    /**
     * You can obtain default config screen based on provided config ID.
     *
     * @param configId ID of your config
     * @param previous Previously open screen
     * @return Either new config screen or {@code null} when no config exists with the provided ID
     */
    @Nullable
    @Environment(EnvType.CLIENT)
    public static Screen getConfigScreen(String configId, Screen previous) {
        return ConfigHolder.getConfig(configId).map(holder -> {
            Map<String, ConfigValue<?>> valueMap = holder.getValueMap();
            return new ConfigScreen(configId, holder.getConfigId(), valueMap, previous);
        }).orElse(null);
    }

    /**
     * Obtain group of multiple configs based on group ID. This is useful when you have multiple config files
     * for your mod.
     *
     * @param group Group ID, usually mod ID
     * @param previous Previously open screen
     * @return Either new config group screen or null when no config exists under the provided group
     */
    @Environment(EnvType.CLIENT)
    public static Screen getConfigScreenByGroup(String group, Screen previous) {
        List<ConfigHolder<?>> list = ConfigHolder.getConfigsByGroup(group);
        if (list.isEmpty())
            return null;
        return getConfigScreenByGroup(list, group, previous);
    }

    @Environment(EnvType.CLIENT)
    private static Screen getConfigScreenByGroup(List<ConfigHolder<?>> group, String groupId, Screen previous) {
        return new ConfigGroupScreen(previous, groupId, group);
    }
}
