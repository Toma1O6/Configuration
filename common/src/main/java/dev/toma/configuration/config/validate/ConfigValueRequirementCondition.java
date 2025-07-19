package dev.toma.configuration.config.validate;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.value.IConfigValueReadable;

public class ConfigValueRequirementCondition implements RequirementCondition {

    private final String config;
    private final String path;
    private final String[] accepts;
    private final boolean invert;

    public ConfigValueRequirementCondition(String config, String path, String[] accepts, boolean invert) {
        this.config = config;
        this.path = path;
        this.accepts = accepts;
        this.invert = invert;
    }

    @Override
    public boolean isEditable(ConfigHolder<?> activeConfig, IConfigValueReadable<?> field) {
        return Configuration.getConfig(this.config)
                .flatMap(holder -> holder.getConfigValue(this.path, Object.class))
                .filter(configValue -> {
                    String value = configValue.toString();
                    for (String acceptedValue : this.accepts) {
                        if (value.equalsIgnoreCase(acceptedValue) && !this.invert) {
                            return true;
                        }
                    }
                    return false;
                })
                .isPresent();
    }
}
