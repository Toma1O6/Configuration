package dev.toma.configuration.io;

import dev.toma.configuration.ConfigHolder;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.format.IConfigFormat;
import dev.toma.configuration.format.IFormattedWriter;
import dev.toma.configuration.value.ConfigValue;
import dev.toma.configuration.value.ConfigValueIdentifier;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public final class ConfigFileIO {

    public static final String CONFIG_DIRECTORY = "./config";
    public static final Marker MARKER = MarkerManager.getMarker("IO");

    public static void readFromFile(ConfigHolder<?> holder) {
        File file = getConfigFile(holder);
        try {
            if (!file.exists()) {
                writeConfigFileFromMemory(holder);
            } else {
                // LOAD FROM FILE
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void writeConfigFileFromMemory(ConfigHolder<?> holder) throws IOException {
        File file = getConfigFile(holder);
        if (file.getParentFile().mkdirs()) {
            Configuration.LOGGER.debug(MARKER, "Initialized directories for configs");
        }
        if (!file.exists()) {
            Configuration.LOGGER.debug(MARKER, "Attempting to create new config file {}", file.getName());
            if (file.createNewFile()) {
                Configuration.LOGGER.debug(MARKER, "New config file created at {}", file.getAbsolutePath());
            } else {
                throw new RuntimeException("Failed to create config file");
            }
        }
        Collection<ConfigValue<?>> values = holder.getValues();
        IConfigFormat format = holder.getFormat();
        IFormattedWriter writer = format.getFormatWriterFactory().create();
        for (ConfigValue<?> value : values) {
            ConfigValue<T> genericValue = (ConfigValue<T>) value;
            ITypeAdapter<T> adapter = genericValue.getAdapter();
            ConfigValueIdentifier identifier = genericValue.getIdentifier();
            adapter.write(writer, identifier.getField(), genericValue.getStoredValue());
        }
        writer.writeIntoFile(file);
    }

    private static File getConfigFile(ConfigHolder<?> holder) {
        ResourceLocation configId = holder.getConfigId();
        return new File(CONFIG_DIRECTORY + "/" + configId.getPath() + "." + holder.getFormat().fileExtension());
    }
}
