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
     * Validates the new value according to defined rules and returns {@link IValidationResult} with result.
     * For valid values you can use the {@link IValidationResult#success()} method.
     *
     * @param newValue Value to set
     * @param wrapper Read-only config value wrapper
     * @return {@link IValidationResult} containing result of this validation
     */
    IValidationResult validate(T newValue, IConfigValueReadable<T> wrapper);
}
