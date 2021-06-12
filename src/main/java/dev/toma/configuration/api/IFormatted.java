package dev.toma.configuration.api;

/**
 * Interface which allows formatting on decimal config types
 * @param <N> Number type
 */
public interface IFormatted<N extends Number> {

    /**
     * Get formatted string for display in UI
     * @return Formatted string
     */
    String getFormatted();

    /**
     * Formats input number
     * @param num Number which will be formatted
     * @return Formatted string
     */
    String formatNumber(N num);
}
