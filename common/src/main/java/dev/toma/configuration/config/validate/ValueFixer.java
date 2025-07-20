package dev.toma.configuration.config.validate;

/**
 * Allows you to further correct the config value which is being assigned. Used mostly internally for number range
 * corrections, array size fixes etc.
 *
 * @author Toma
 * @since 4.0
 * @param <V> The config value type
 */
@FunctionalInterface
public interface ValueFixer<V> {

    /**
     * Takes the input value and returns modified value if needed
     * @param value Input value which is being assigned
     * @return The modified value
     */
    V fixValue(V value);
}
