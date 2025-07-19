package dev.toma.configuration.config.validate;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.value.IConfigValueReadable;

public class ModRequirementCondition implements RequirementCondition {

    private final String modId;
    private final boolean invert;

    public ModRequirementCondition(String modId, boolean invert) {
        this.modId = modId;
        this.invert = invert;
    }

    @Override
    public boolean isEditable(ConfigHolder<?> activeConfig, IConfigValueReadable<?> field) {
        return Configuration.PLATFORM.isModLoaded(this.modId) && !this.invert;
    }
}
