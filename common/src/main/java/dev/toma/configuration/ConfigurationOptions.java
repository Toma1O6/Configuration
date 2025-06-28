package dev.toma.configuration;

import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;

@Config(id = Configuration.MODID)
public class ConfigurationOptions {

    @Configurable("text.configuration.options.advanced_mode")
    public boolean advancedMode = false;

    @Configurable("text.configuration.options.hide_background")
    public boolean hideBackground = false;
}
