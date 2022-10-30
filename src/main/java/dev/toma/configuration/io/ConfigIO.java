package dev.toma.configuration.io;

import dev.toma.configuration.ConfigHolder;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.format.IConfigFormatHandler;
import dev.toma.configuration.exception.ConfigReadException;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.io.IOException;

public final class ConfigIO {

    public static final Marker MARKER = MarkerManager.getMarker("IO");

    public static void processConfig(ConfigHolder<?> holder) {
        Configuration.LOGGER.debug(MARKER, "Starting processing of config {}", holder.getConfigId());
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
        Configuration.LOGGER.debug(MARKER, "Processing of config {} has finished", holder.getConfigId());
    }

    public static void readConfig(ConfigHolder<?> holder) throws IOException {
        Configuration.LOGGER.debug(MARKER, "Reading config {}", holder.getConfigId());
        IConfigFormat format = holder.getFormat().createFormat();
        File file = getConfigFile(holder);
        try {
            format.readFile(file);
            holder.values().forEach(value -> value.deserializeValue(format));
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
}
