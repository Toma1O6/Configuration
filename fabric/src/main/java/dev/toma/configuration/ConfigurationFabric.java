package dev.toma.configuration;

import dev.toma.configuration.command.ConfigSaveCommand;
import dev.toma.configuration.config.io.ConfigurationFileManager;
import dev.toma.configuration.network.FabricNetworkManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.dedicated.DedicatedServer;

public class ConfigurationFabric implements ModInitializer {

    public ConfigurationFabric() {
        Configuration.setup();
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            if (server instanceof DedicatedServer) {
                ConfigurationFileManager.FILE_WATCH_MANAGER.stop();
            }
            ConfigurationFileManager.serverStopping();
        });
        ServerLifecycleEvents.SERVER_STARTED.register(server -> ConfigurationFileManager.serverStarted());
    }

    @Override
    public void onInitialize() {
        ConfigurationFileManager.FILE_WATCH_MANAGER.startService();
        FabricNetworkManager.INSTANCE.registerMessages();
        CommandRegistrationCallback.EVENT.register((dispatcher, context, environment) -> ConfigSaveCommand.register(dispatcher));
    }
}
