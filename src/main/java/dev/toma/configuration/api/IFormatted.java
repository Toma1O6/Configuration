package dev.toma.configuration.api;

/**
 * Interface which allows formatting on decimal config types
 * @param <T> Formatting type
 */
public interface IFormatted<T> {

    /**
     * Get formatted string for display in UI
     * @return Formatted string
     */
    String formatConfigValue();

    /**
     * Formats input number
     * @param t Object which will be formatted
     * @return Formatted string
     */
    String format(T t);
}
