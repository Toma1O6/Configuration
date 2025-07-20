package dev.toma.configuration;

import com.mojang.brigadier.CommandDispatcher;
import dev.toma.configuration.client.ConfigurationClient;
import dev.toma.configuration.command.ConfigSaveCommand;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.io.ConfigurationFileManager;
import dev.toma.configuration.network.ForgeNetworkManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;
import java.util.Map;

@Mod(Configuration.MODID)
public class ConfigurationForge {

    public ConfigurationForge(FMLJavaModLoadingContext context) {
        Configuration.setup();

        BusGroup modBusGroup = context.getModBusGroup();
        FMLCommonSetupEvent.getBus(modBusGroup).addListener(this::init);
        FMLClientSetupEvent.getBus(modBusGroup).addListener(this::clientInit);

        ServerStoppingEvent.BUS.addListener(this::serverStopping);
        ServerStartedEvent.BUS.addListener(this::serverStarting);
        RegisterCommandsEvent.BUS.addListener(this::registerCommands);
    }

    private void init(FMLCommonSetupEvent event) {
        ConfigurationFileManager.FILE_WATCH_MANAGER.startService();
        ForgeNetworkManager.registerMessages();
    }

    private void serverStarting(ServerStartedEvent event) {
        ConfigurationFileManager.serverStarted();
    }

    private void serverStopping(ServerStoppingEvent event) {
        ConfigurationFileManager.FILE_WATCH_MANAGER.stop();
        ConfigurationFileManager.serverStopping();
    }

    private void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        ConfigSaveCommand.register(dispatcher);
    }

    private void clientInit(FMLClientSetupEvent event) {
        Map<String, List<ConfigHolder<?>>> groups = ConfigHolder.getConfigGroupingByGroup();
        ModList modList = ModList.get();
        for (Map.Entry<String, List<ConfigHolder<?>>> entry : groups.entrySet()) {
            String modId = entry.getKey();
            ModContainer container = modList.getModContainerById(modId).orElse(null);
            if (container != null) {
                List<ConfigHolder<?>> list = entry.getValue();
                container.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> {
                    if (list.size() == 1) {
                        return ConfigurationClient.getConfigScreen(list.getFirst().getConfigId(), screen);
                    }
                    return ConfigurationClient.getConfigScreenByGroup(list, modId, screen);
                }));
            }
        }
    }
}
