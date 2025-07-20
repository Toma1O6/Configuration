package dev.toma.configuration.config.validate;

import dev.toma.configuration.config.io.ConfigurationFileManager;
import dev.toma.configuration.config.value.IConfigValueReadable;
import net.minecraft.network.chat.Component;

public class GameRestartValidator<V> implements Validator<V> {

    public static final Component GAME_RESTART_REQUIRED = Component.translatable("text.configuration.validation.restart_required");

    @Override
    public ValidationResult validate(V newValue, IConfigValueReadable<V> valueHolder) {
        if (ConfigurationFileManager.getEnvironment() == ConfigurationFileManager.ConfigEnvironment.LOADING) {
            return ValidationResult.success();
        }
        V activeValue = valueHolder.get(IConfigValueReadable.Mode.SAVED);
        if (this.isChanged(valueHolder.isEditable(), newValue, activeValue)) {
            return ValidationResult.warning(GAME_RESTART_REQUIRED);
        }
        return ValidationResult.success();
    }

    private boolean isChanged(boolean editMode, V v1, V v2) {
        return editMode && !v2.equals(v1);
    }
}
