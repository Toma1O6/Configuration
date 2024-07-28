package dev.toma.configuration.config.value;

import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

/**
 * Config value wrapper. Holds data such as validations, memory references and so on.
 * @param <T> Contained value type
 *
 * @author Toma
 * @since 3.0
 */
public interface IConfigValue<T> extends Supplier<T> {

    // TODO methods to attach listeners, assign custom description provider, custom gui warnings

    /**
     * @return Currently set or {@link Mode#PENDING} value. Calls internally the {@link IConfigValue#get(Mode)} method with
     * {@link Mode#PENDING} attribute.
     */
    @Override
    default T get() {
        return this.get(Mode.PENDING);
    }

    /**
     * Obtain config value for specific mode.
     * @param mode The {@link Mode} for config value.
     * @apiNote This can also return neither of the set/pending values, but instead value received from server for
     *          {@link dev.toma.configuration.config.Configurable.Synchronized} values when active on server. Also, active
     *          value is returned for {@link Mode#PENDING} when no unsaved value is present, so this will <b>never</b> return {@code null}
     * @return The currently held value for given {@link Mode}
     */
    T get(Mode mode);

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
     * @return Whether current config value is not saved
     */
    boolean isChanged();

    /**
     * @return Whether current config value does not match default value
     */
    boolean isChangedFromDefault();

    /**
     * @return Whether current config value can be edited
     */
    boolean isEditable();

    /**
     * @return Localized title for this config value
     */
    Component getTitle();

    /**
     * @return Array of comments saved into file
     */
    String[] getFileComments();

    /**
     * @return Parent value of this config value or {@code null} for top level config values
     */
    IConfigValue<?> parent();

    /**
     * @return ID of this single value
     * @apiNote Does not contain full path of the field, so this cannot be used for example for obtaining {@link IConfigValue} references.
     * Instead, you have to use the {@link IConfigValue#getPath()} for that.
     */
    String getId();

    /**
     * @return Full field path composed of parent IDs and this config value ID separated by {@code .} characters.
     *         For example {@code topLevelField.nestedField.thisField}
     */
    String getPath();

    /**
     * Save state modes for config values.
     */
    enum Mode {

        /** Used to obtain currently active config value */
        SAVED,

        /** Used to obtain currently unsaved config value which is not active yet */
        PENDING
    }
}
