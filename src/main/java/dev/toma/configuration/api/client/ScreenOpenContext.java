package dev.toma.configuration.api.client;

import dev.toma.configuration.api.ModConfig;

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
