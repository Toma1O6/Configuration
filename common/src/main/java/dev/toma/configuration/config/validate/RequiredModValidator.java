package dev.toma.configuration.config.validate;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.value.IConfigValueReadable;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;

public class RequiredModValidator<V> implements Validator<V> {

    private static final String KEY_MISSING_MOD = "text.configuration.validation.missing_mod";
    private static final String KEY_ACTIVE_MOD = "text.configuration.validation.active_mod";

    private final String modId;
    private final String modName;
    private final boolean loaded;

    public RequiredModValidator(Configurable.DependsOn.ActiveMod mod) {
        this(mod.value(), mod.displayName(), mod.loaded());
    }

    public RequiredModValidator(String modId, String modName, boolean loaded) {
        this.modId = modId;
        this.modName = modName;
        this.loaded = loaded;
    }

    @Override
    public ValidationResult validate(V newValue, IConfigValueReadable<V> valueHolder) {
        return Configuration.PLATFORM.isModLoaded(this.modId) && this.loaded
                ? ValidationResult.success()
                : ValidationResult.warning(this.createWarningLabel());
    }

    private Component createWarningLabel() {
        String key = this.loaded ? KEY_ACTIVE_MOD : KEY_MISSING_MOD;
        String displayName = StringUtils.isNotBlank(this.modName) ?  this.modName : this.modId;
        return Component.translatable(key, displayName);
    }
}
