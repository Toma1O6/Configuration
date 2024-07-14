package dev.toma.configuration;

import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;

@Config(id = Configuration.MODID)
@Config.Version(version = 1)
public final class ConfigurationConfig {

    @Configurable(localization = Configurable.LocalizationPath.FULL)
    @Configurable.Comment(localize = true, value = {"Allow config version checks", "Will also disable the GUI prompts for users"})
    public boolean allowConfigVersioning = true;
}
