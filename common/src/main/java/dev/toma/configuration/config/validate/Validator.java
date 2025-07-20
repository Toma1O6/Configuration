package dev.toma.configuration.config.validate;

import dev.toma.configuration.config.value.IConfigValue;
import dev.toma.configuration.config.value.IConfigValueReadable;

/**
 * Value validator for config fields
 *
 * @param <V> Type of held config value
 * @author Toma
 * @since 4.0
 */
public interface Validator<V> {

    /**
     * Validates the new value according to defined rules and returns {@link ValidationResult} with result.
     * For valid values you can use the {@link ValidationResult#success()} method.
     *
     * @param newValue Value to set
     * @param valueHolder Read-only config value
     * @return {@link ValidationResult} containing result of this validation
     */
    ValidationResult validate(V newValue, IConfigValueReadable<V> valueHolder);

    default void onGameLoaded(IConfigValue<V> value) {
    }
}
