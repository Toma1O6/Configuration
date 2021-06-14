package dev.toma.configuration.api;

import dev.toma.configuration.api.type.ObjectType;

public class ModConfig extends ObjectType {

    private final IConfigPlugin plugin;

    public ModConfig(IObjectSpec spec, IConfigPlugin plugin) {
        super(spec);
        this.plugin = plugin;
    }

    public IConfigPlugin getPlugin() {
        return plugin;
    }
}
