package dev.toma.configuration.api;

/**
 * Interface which allows formatting on decimal config types
 */
public interface IFormatted {

    /**
     * Get formatted string for display in UI
     * @return Formatted string
     */
    String formatConfigValue();

    /**
     * Formats input number
     * @param value Object which will be formatted
     * @return Formatted string
     */
    String format(Object value);
}
