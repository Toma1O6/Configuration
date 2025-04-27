package dev.toma.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

@SuppressWarnings("ResultOfMethodCallIgnored")
public final class ConfigurationSettings {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final File OPTIONS_FILE = new File("./config/configuration-options.json");
    private static final ConfigurationSettings INSTANCE = new ConfigurationSettings();

    /** Allows you to display advanced fields defined by mod configs in GUI */
    private boolean advancedMode = false;
    /** Allows to hide config screen background, can be useful for overlay configurations and so on. Does nothing in main menu */
    private boolean hideBackground = false;

    public static ConfigurationSettings getInstance() {
        return INSTANCE;
    }

    public static void loadSettings() {
        try {
            OPTIONS_FILE.getParentFile().mkdirs();
            if (!OPTIONS_FILE.exists()) {
                saveSettings();
                return;
            }
            try (FileReader reader = new FileReader(OPTIONS_FILE)) {
                ConfigurationSettings settings = GSON.fromJson(reader, ConfigurationSettings.class);
                INSTANCE.setAdvancedMode(settings.isAdvancedMode());
                INSTANCE.setHideBackground(settings.isHideBackground());
            }
        } catch (Exception e) {
            Configuration.LOGGER.error("Failed to load configuration options, defaults will be used", e);
        }
    }

    public static void saveSettings() {
        try {
            OPTIONS_FILE.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(OPTIONS_FILE)) {
                GSON.toJson(INSTANCE, writer);
            }
        } catch (Exception e) {
            Configuration.LOGGER.error("Failed to save configuration options", e);
        }
    }

    public void setAdvancedMode(boolean advancedMode) {
        this.advancedMode = advancedMode;
    }

    public boolean isAdvancedMode() {
        return advancedMode;
    }

    public void setHideBackground(boolean hideBackground) {
        this.hideBackground = hideBackground;
    }

    public boolean isHideBackground() {
        return hideBackground;
    }
}
