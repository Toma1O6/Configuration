package dev.toma.configuration;


import com.mojang.brigadier.CommandDispatcher;
import dev.toma.configuration.client.ConfigurationClient;
import dev.toma.configuration.command.ConfigSaveCommand;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.io.ConfigIO;
import dev.toma.configuration.network.NeoforgeNetworkManager;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@Mod(Configuration.MODID)
public class ConfigurationNeoforge {

    public ConfigurationNeoforge(IEventBus eventBus) {
        Configuration.setup();

        eventBus.addListener(this::init);
        eventBus.addListener(this::clientInit);
        eventBus.addListener(NeoforgeNetworkManager.INSTANCE::registerMessages);
        NeoForge.EVENT_BUS.addListener(this::serverStopping);
        NeoForge.EVENT_BUS.addListener(this::serverStarted);
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
    }

    private void clientInit(FMLClientSetupEvent event) {
        Map<String, List<ConfigHolder<?>>> groups = ConfigHolder.getConfigGroupingByGroup();
        ModList modList = ModList.get();
        for (Map.Entry<String, List<ConfigHolder<?>>> entry : groups.entrySet()) {
            String modId = entry.getKey();
            Optional<? extends ModContainer> optional = modList.getModContainerById(modId);
            optional.ifPresent(modContainer -> {
                List<ConfigHolder<?>> list = entry.getValue();
                modContainer.registerExtensionPoint(IConfigScreenFactory.class, (Supplier<IConfigScreenFactory>) () -> (container, screen) -> {
                    if (list.size() == 1) {
                        return ConfigurationClient.getConfigScreen(list.getFirst().getConfigId(), screen);
                    }
                    return ConfigurationClient.getConfigScreenByGroup(list, modId, screen);
                });
            });
        }
    }

    private void init(FMLCommonSetupEvent event) {
        ConfigIO.FILE_WATCH_MANAGER.startService();
    }

    private void serverStarted(ServerStartedEvent event) {
        ConfigIO.serverStarted();
    }

    private void serverStopping(ServerStoppingEvent event) {
        ConfigIO.FILE_WATCH_MANAGER.stop();
        ConfigIO.serverStopping();
    }

    private void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        ConfigSaveCommand.register(dispatcher);
    }
}