package dev.toma.configuration;

import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;

@Config(id = "debug", group = Configuration.MODID)
public final class ArrayDebugConfig {

    @Configurable
    public int[] arr = { 1, 10 };
}
