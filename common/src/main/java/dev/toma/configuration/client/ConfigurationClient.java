package dev.toma.configuration.client;

import dev.toma.configuration.client.screen.ConfigGroupScreen;
import dev.toma.configuration.client.screen.ConfigScreen;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.theme.ConfigurationThemes;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.value.ConfigValue;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Client API entry point for Configuration. Allows you to obtain config screens or configure custom config themes.
 *
 * @author Toma
 * @since 2.3.0
 */
public final class ConfigurationClient {

    @ApiStatus.Internal
    private static final Map<String, ConfigTheme> CONFIG_THEMES = new HashMap<>();

    /**
     * You can obtain default config screen based on provided config class.
     *
     * @param configClass Your config class
     * @param previous    Previously open screen
     * @return Either new config screen or {@code null} when no config exists for the provided class
     */
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
    public static Screen getConfigScreen(String configId, Screen previous) {
        return ConfigHolder.getConfig(configId).map(holder -> {
            Map<String, ConfigValue<?>> valueMap = holder.getValueMap();
            return new ConfigScreen(holder, holder.getTitle(), valueMap, previous);
        }).orElse(null);
    }

    /**
     * Obtain group of multiple configs based on group ID. This is useful when you have multiple config files
     * for your mod.
     *
     * @param group    Group ID, usually mod ID
     * @param previous Previously open screen
     * @return Either new config group screen or null when no config exists under the provided group
     */
    public static Screen getConfigScreenByGroup(String group, Screen previous) {
        List<ConfigHolder<?>> list = ConfigHolder.getConfigsByGroup(group);
        if (list.isEmpty())
            return null;
        return getConfigScreenByGroup(list, group, previous);
    }

    /**
     * Allows you to set custom configuration theme for your config holder
     * @param holder Your config holder
     * @param themeConfiguration Theme configuration builder. The consumed instance is currently set theme, so default config theme in most cases.
     * @since 3.0
     */
    public static void setCustomConfigTheme(ConfigHolder<?> holder, Consumer<ConfigTheme> themeConfiguration) {
        ConfigTheme theme = getConfigTheme(holder).copy();
        themeConfiguration.accept(theme);
        CONFIG_THEMES.put(holder.getConfigId(), theme);
    }

    /**
     * Get actual config theme for your config
     * @param holder Your config holder
     * @return Stored custom config theme
     * @since 3.0
     */
    public static ConfigTheme getConfigTheme(ConfigHolder<?> holder) {
        return CONFIG_THEMES.computeIfAbsent(holder.getConfigId(), id -> {
            ConfigTheme theme = new ConfigTheme();
            ConfigurationThemes.DEFAULT_THEME.accept(theme);
            return theme;
        });
    }

    @ApiStatus.Internal
    public static Screen getConfigScreenByGroup(List<ConfigHolder<?>> group, String groupId, Screen previous) {
        return new ConfigGroupScreen(previous, groupId, group);
    }
}
