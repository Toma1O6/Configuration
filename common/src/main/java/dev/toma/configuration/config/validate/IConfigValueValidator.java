package dev.toma.configuration.config.validate;

import dev.toma.configuration.config.value.IConfigValueReadable;

/**
 * Value validator for config fields
 *
 * @param <T> Config value type
 * @since 3.0
 * @author Toma
 */
public interface IConfigValueValidator<T> {

    /**
     * Validates the new value according to defined rules and returns {@link ValidationResult} with result.
     * For valid values you can use the {@link ValidationResult#success()} method.
     *
     * @param newValue Value to set
     * @param wrapper Read-only config value wrapper
     * @return {@link ValidationResult} containing result of this validation
     */
    ValidationResult validate(T newValue, IConfigValueReadable<T> wrapper);
}
