package dev.toma.configuration.config.test;

import dev.toma.configuration.config.Config;

@Config(id = "configuration-properties", filename = "tests/properties-test", group = "configuration")
@Config.NoAutoSync
public final class PropertiesConfig extends AbstractConfig {

}
