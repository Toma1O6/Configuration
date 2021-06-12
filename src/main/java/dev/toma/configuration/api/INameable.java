package dev.toma.configuration.api;

/**
 * @author Toma
 */
public interface INameable {

    /**
     * Used as identifier/key
     * @return an unformatted name
     */
    String getUnformattedName();

    /**
     * Used inside UIs
     * @return a formatted name
     */
    String getFormattedName();
}
