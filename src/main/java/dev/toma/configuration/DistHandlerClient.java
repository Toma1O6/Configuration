package dev.toma.configuration;

import dev.toma.configuration.api.IConfigPlugin;
import dev.toma.configuration.api.ModConfig;
import dev.toma.configuration.api.client.IClientSettings;
import dev.toma.configuration.api.client.IScreenFactory;
import dev.toma.configuration.api.client.ScreenOpenContext;
import dev.toma.configuration.api.type.ObjectType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fmlclient.ConfigGuiHandler;

import java.util.Optional;

public class DistHandlerClient implements IDistHandler {

    @OnlyIn(Dist.CLIENT)
    @Override
    public void runConfigSetup(ModConfig config) {
        IConfigPlugin plugin = config.getPlugin();
        IClientSettings settings = config.settings();
        IScreenFactory<ObjectType> factory = settings.getConfigScreenFactory();
        ModList list = ModList.get();
        Optional<? extends ModContainer> optionalModContainer = list.getModContainerById(plugin.getModID());
        ScreenOpenContext ctx = ScreenOpenContext.of(config);
        optionalModContainer.ifPresent(container -> container.registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () -> new ConfigGuiHandler.ConfigGuiFactory((minecraft, screen) -> factory.createScreen(screen, config, ctx))));
        plugin.setupClient(settings);
    }
}
