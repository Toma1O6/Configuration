package dev.toma.configuration;

import com.mojang.brigadier.CommandDispatcher;
import dev.toma.configuration.client.ConfigurationClient;
import dev.toma.configuration.command.ConfigSaveCommand;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.io.ConfigIO;
import dev.toma.configuration.network.ForgeNetworkManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mod(Configuration.MODID)
public class ConfigurationForge {

    public ConfigurationForge() {
        Configuration.setup();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::init);
        modEventBus.addListener(this::clientInit);

        IEventBus eventBus = MinecraftForge.EVENT_BUS;
        eventBus.addListener(this::serverStopping);
        eventBus.addListener(this::serverStarting);
        eventBus.addListener(this::registerCommands);
    }

    private void init(FMLCommonSetupEvent event) {
        ConfigIO.FILE_WATCH_MANAGER.startService();
        ForgeNetworkManager.registerMessages();
    }

    private void serverStarting(ServerStartedEvent event) {
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

    private void clientInit(FMLClientSetupEvent event) {
        Map<String, List<ConfigHolder<?>>> groups = ConfigHolder.getConfigGroupingByGroup();
        ModList modList = ModList.get();
        for (Map.Entry<String, List<ConfigHolder<?>>> entry : groups.entrySet()) {
            String modId = entry.getKey();
            Optional<? extends ModContainer> optional = modList.getModContainerById(modId);
            optional.ifPresent(modContainer -> {
                List<ConfigHolder<?>> list = entry.getValue();
                modContainer.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> {
                    if (list.size() == 1) {
                        return ConfigurationClient.getConfigScreen(list.getFirst().getConfigId(), screen);
                    }
                    return ConfigurationClient.getConfigScreenByGroup(list, modId, screen);
                }));
            });
        }
    }
}
