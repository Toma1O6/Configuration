package dev.toma.configuration.config.io;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.exception.ConfigReadException;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.format.IConfigFormatHandler;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.io.IOException;

public final class ConfigIO {

    public static final Marker MARKER = MarkerManager.getMarker("IO");
    public static final FileWatchManager FILE_WATCH_MANAGER = new FileWatchManager();
    private static ConfigEnvironment environment = ConfigEnvironment.LOADING;

    public static void processConfig(ConfigHolder<?> holder) {
        Configuration.LOGGER.debug(MARKER, "Starting processing of config {}", holder.getConfigId());
        processSafely(holder, () -> {
            File file = getConfigFile(holder);
            if (file.exists()) {
                try {
                    readConfig(holder);
                } catch (IOException e) {
                    Configuration.LOGGER.error(MARKER, "Config read failed for config ID {}, will create default config file", holder.getConfigId());
                }
            }
            try {
                writeConfig(holder);
            } catch (IOException e) {
                Configuration.LOGGER.fatal(MARKER, "Couldn't write config {}, aborting mod startup", holder.getConfigId());
                throw new RuntimeException("Config write failed", e);
            }
        });
        Configuration.LOGGER.debug(MARKER, "Processing of config {} has finished", holder.getConfigId());
    }

    public static void reloadClientValues(ConfigHolder<?> configHolder) {
        processSafely(configHolder, () -> {
            try {
                readConfig(configHolder);
            } catch (IOException e) {
                Configuration.LOGGER.error(MARKER, "Failed to read config file {}", configHolder.getConfigId());
            }
        });
    }

    public static void saveClientValues(ConfigHolder<?> configHolder) {
        processSafely(configHolder, () -> {
            try {
                configHolder.save();
                writeConfig(configHolder);
            } catch (IOException e) {
                Configuration.LOGGER.error(MARKER, "Failed to write config file {}", configHolder.getConfigId());
            }
        });
    }

    private static void processSafely(ConfigHolder<?> holder, Runnable action) {
        try {
            synchronized (holder.getLock()) {
                action.run();
            }
        } catch (Exception e) {
            Configuration.LOGGER.fatal(MARKER, "Error loading config {} due to critical error '{}'. Report this issue to this config's owner!", holder.getConfigId(), e.getMessage());
            throw new ReportedException(CrashReport.forThrowable(e, "Config " + holder.getConfigId() + " failed. Report issue to config owner"));
        }
    }

    private static void readConfig(ConfigHolder<?> holder) throws IOException {
        Configuration.LOGGER.debug(MARKER, "Reading config {}", holder.getConfigId());
        IConfigFormat format = holder.getFormat().createFormat();
        File file = getConfigFile(holder);
        if (!file.exists())
            return;
        try {
            format.readFile(file);
            holder.values().forEach(value -> value.deserializeValue(format));
            holder.save();
        } catch (ConfigReadException e) {
            Configuration.LOGGER.error(MARKER, "Config read failed, using default values", e);
        }
    }

    public static void writeConfig(ConfigHolder<?> holder) throws IOException {
        Configuration.LOGGER.debug(MARKER, "Writing config {}", holder.getConfigId());
        File file = getConfigFile(holder);
        File dir = file.getParentFile();
        if (dir.mkdirs()) {
            Configuration.LOGGER.debug(MARKER, "Created file directories at {}", dir.getAbsolutePath());
        }
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new RuntimeException("Config file create failed");
            }
        }
        IConfigFormatHandler handler = holder.getFormat();
        IConfigFormat format = handler.createFormat();
        holder.values().forEach(value -> value.serializeValue(format));
        format.writeFile(file);
    }

    public static File getConfigFile(ConfigHolder<?> holder) {
        IConfigFormatHandler handler = holder.getFormat();
        String filename = holder.getFilename();
        return new File("./config/" + filename + "." + handler.fileExt());
    }

    public static void serverStarted() {
        setEnvironment(ConfigEnvironment.PLAYING);
    }

    public static void serverStopping() {
        setEnvironment(ConfigEnvironment.MENU);
    }

    public static ConfigEnvironment getEnvironment() {
        return environment;
    }

    public static void setEnvironment(ConfigEnvironment environment) {
        ConfigIO.environment = environment;
        Configuration.LOGGER.debug(MARKER, "Setting configuration environment to {}", environment);
    }

    public enum ConfigEnvironment {
        LOADING,
        MENU,
        PLAYING
    }
}
