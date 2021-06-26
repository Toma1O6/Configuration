package dev.toma.configuration.api.client;

import dev.toma.configuration.api.ModConfig;

/**
 * Simple structure containing mod config to pass around.
 * Use {@link ScreenOpenContext#getModConfig()} to obtain mod config instance.
 * Also implements {@link IModID} interface, so it can be easily used for config updates
 * as it requires ModID
 */
public final class ScreenOpenContext implements IModID {

    private final ModConfig config;

    private ScreenOpenContext(ModConfig config) {
        this.config = config;
    }

    public static ScreenOpenContext of(ModConfig config) {
        return new ScreenOpenContext(config);
    }

    public ModConfig getModConfig() {
        return config;
    }

    @Override
    public String getModID() {
        return config.getPlugin().getModID();
    }
}
