package dev.toma.configuration.config.value;

import dev.toma.configuration.config.util.IDescriptionProvider;
import dev.toma.configuration.config.validate.IConfigValueValidator;

/**
 * Config value wrapper. Holds data such as validations, memory references and so on.
 * @param <T> Contained value type
 *
 * @author Toma
 * @since 3.0
 */
public interface IConfigValue<T> extends IConfigValueReadable<T> {

    /**
     * Set new value for config file. Value is not applied until succesfully saved, which may be blocked given
     * {@link dev.toma.configuration.config.Configurable.UpdateRestriction} value and current configuration environment.
     * @param value New value to set
     */
    void setValue(T value);

    /**
     * Drops unsaved changes
     */
    void revertChanges();

    /**
     * Sets active value to default value
     */
    void revertChangesToDefault();

    /**
     * Saves currently pending values if applicable
     */
    void save();

    /**
     * @return Whether current config value can be edited
     */
    boolean isEditable();

    /**
     * @return Parent value of this config value or {@code null} for top level config values
     */
    IConfigValue<?> parent();

    /**
     * Allows you to attach custom description provider for this config value.
     * @param provider The new description provider
     */
    void addDescriptionProvider(IDescriptionProvider<T> provider);

    /**
     * Allows you to register custom value validator for this config value. All value assignments are validated via
     * {@link IConfigValueValidator#validate(Object, IConfigValueReadable)} method which exposes both the value which is
     * being set along with read-only config value instance.
     *
     * @param validator The validator to be registered
     * @throws UnsupportedOperationException When attempting to register validator on Object values
     */
    void addValidator(IConfigValueValidator<T> validator);
}
