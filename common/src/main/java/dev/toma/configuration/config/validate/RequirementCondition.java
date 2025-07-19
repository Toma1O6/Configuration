package dev.toma.configuration.config.validate;

import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.value.IConfigValueReadable;

public interface RequirementCondition {

    // TODO introduce new data type to hold result + localizable reason
    boolean isEditable(ConfigHolder<?> activeConfig, IConfigValueReadable<?> field);
}
