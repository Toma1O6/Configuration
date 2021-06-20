package dev.toma.configuration.api;

import dev.toma.configuration.api.client.IClientSettings;
import dev.toma.configuration.api.type.ObjectType;
import dev.toma.configuration.client.ClientSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

public class ModConfig extends ObjectType {

    private final IConfigPlugin plugin;
    private final IClientSettings clientSettings;

    public ModConfig(IObjectSpec spec, IConfigPlugin plugin) {
        super(spec);
        this.plugin = plugin;
        this.clientSettings = DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> ClientSettings::new);
    }

    public IConfigPlugin getPlugin() {
        return plugin;
    }

    @OnlyIn(Dist.CLIENT)
    public IClientSettings settings() {
        return clientSettings;
    }
}
