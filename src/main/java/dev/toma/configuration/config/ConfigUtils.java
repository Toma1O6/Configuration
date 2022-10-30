package dev.toma.configuration.config;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.io.ConfigIO;

import javax.annotation.Nullable;
import java.util.Objects;

public final class ConfigUtils {

    public static void logCorrectedMessage(String field, @Nullable Object prevValue, Object corrected) {
        Configuration.LOGGER.warn(ConfigIO.MARKER, "Correcting config value '{}' from '{}' to '{}'", field, Objects.toString(prevValue), corrected);
    }
}
