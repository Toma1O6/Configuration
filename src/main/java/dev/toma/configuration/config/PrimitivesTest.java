package dev.toma.configuration.config;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.annotation.Config;

@Config(value = Configuration.MODID, filename = "configuration-primitives")
public final class PrimitivesTest {

    @Config.Entry
    public final boolean myBooleanValue = true;
}
