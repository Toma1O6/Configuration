package dev.toma.configuration.client;

import dev.toma.configuration.network.Networking;
import net.fabricmc.api.ClientModInitializer;

public class ConfigurationClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Networking.PacketRegistry.registerClient();
    }
}
