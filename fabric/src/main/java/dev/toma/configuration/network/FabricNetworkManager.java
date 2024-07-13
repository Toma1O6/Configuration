package dev.toma.configuration.network;

import dev.toma.configuration.network.message.S2C_SendConfigDataMessage;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public class FabricNetworkManager implements NetworkManager {

    public static final FabricNetworkManager INSTANCE = new FabricNetworkManager();

    public void registerMessages() {
        PayloadTypeRegistry.playS2C().register(S2C_SendConfigDataMessage.TYPE, S2C_SendConfigDataMessage.CODEC);

        ClientPlayNetworking.registerGlobalReceiver(S2C_SendConfigDataMessage.TYPE, (payload, context) -> payload.receive());
    }

    @Override
    public void dispatchClientMessage(ServerPlayer player, CustomPacketPayload message) {
        ServerPlayNetworking.send(player, message);
    }

    private FabricNetworkManager() {}
}
