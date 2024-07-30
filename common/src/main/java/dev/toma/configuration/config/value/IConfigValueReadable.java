package dev.toma.configuration.config.value;

import dev.toma.configuration.config.validate.IValidationResult;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * Read only methods for all config values
 * @param <T> Contained value type
 *
 * @author Toma
 * @since 3.0
 */
public interface IConfigValueReadable<T> extends Supplier<T> {

    /**
     * @return Currently set or {@link IConfigValue.Mode#PENDING} value. Calls internally the {@link IConfigValue#get(IConfigValue.Mode)} method with
     * {@link IConfigValue.Mode#PENDING} attribute.
     */
    @Override
    default T get() {
        return this.get(Mode.PENDING);
    }

    /**
     * Obtain config value for specific mode.
     * @param mode The {@link IConfigValue.Mode} for config value.
     * @apiNote This can also return neither of the set/pending values, but instead value received from server for
     *          {@link dev.toma.configuration.config.Configurable.Synchronized} values when active on server. Also, active
     *          value is returned for {@link IConfigValue.Mode#PENDING} when no unsaved value is present, so this will <b>never</b> return {@code null}
     * @return The currently held value for given {@link IConfigValue.Mode}
     */
    T get(IConfigValue.Mode mode);

    /**
     * @return Whether current config value is not saved
     */
    boolean isChanged();

    /**
     * @return Whether current config value does not match default value
     */
    boolean isChangedFromDefault();

    /**
     * @return Array of comments saved into file
     */
    String[] getFileComments();

    /**
     * @return Parent value of this config value or {@code null} for top level config values
     */
    IConfigValueReadable<?> parent();

    /**
     * @return Collection of all children config value IDs or empty collection when no children values exist
     */
    Collection<String> getChildrenKeys();

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
     * @return Localized title for this config value
     */
    Component getTitle();

    /**
     * Obtains currently set value validation result. Used only to display additional errors/warnings on GUI
     * so ability to define "valid" (non-null in this case) validation results does not make sense.
     * @return Validation result of this config value or {@code null} when value is valid
     */
    IValidationResult getValidationResult();

    /**
     * @return Description to be rendered for this config value
     */
    List<Component> getDescription();

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
