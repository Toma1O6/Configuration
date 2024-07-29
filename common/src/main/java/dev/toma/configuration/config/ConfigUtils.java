package dev.toma.configuration.config;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.client.widget.EditBoxWidget;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.io.ConfigIO;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Objects;

public final class ConfigUtils {

    public static void logCorrectedMessage(String field, Object prevValue, Object corrected) {
        Configuration.LOGGER.warn(ConfigIO.MARKER, "Correcting config value '{}' from '{}' to '{}'", field, Objects.toString(prevValue), corrected);
    }

    public static void logArraySizeCorrectedMessage(String field, Object prevValue, Object corrected) {
        Configuration.LOGGER.warn(ConfigIO.MARKER, "Correcting config array value '{}' due to invalid size from '{}' to '{}'", field, prevValue, corrected);
    }

    public static <E extends Enum<E>> E getEnumConstant(String value, Class<E> declaringClass) throws ConfigValueMissingException {
        E[] constants = declaringClass.getEnumConstants();
        for (E e : constants) {
            if (e.name().equals(value)) {
                return e;
            }
        }
        throw new ConfigValueMissingException("Missing enum value: " + value);
    }

    public static DecimalFormat getDecimalFormat(Field field) {
        Configurable.Gui.NumberFormat format = field.getAnnotation(Configurable.Gui.NumberFormat.class);
        if (format != null) {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator('.');
            return new DecimalFormat(format.value(), symbols);
        }
        return null;
    }

    public static void adjustCharacterLimit(Field field, EditBoxWidget widget) {
        Configurable.Gui.CharacterLimit limit = field.getAnnotation(Configurable.Gui.CharacterLimit.class);
        if (limit != null) {
            widget.setMaxLength(Math.max(limit.value(), 1));
        }
    }
}
