package dev.toma.configuration.config.validate;

/**
 * TODO
 *
 * @author Toma
 * @since 4.0
 * @param <V>
 */
public interface ValueFixer<V> {

    V fixValue(V value);
}
