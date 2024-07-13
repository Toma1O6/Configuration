package dev.toma.configuration.network;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.network.message.S2C_SendConfigDataMessage;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class NeoforgeNetworkManager implements NetworkManager {

    public static final NeoforgeNetworkManager INSTANCE = new NeoforgeNetworkManager();

    public void registerMessages(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(Configuration.MODID);

        registrar.playToClient(S2C_SendConfigDataMessage.TYPE, S2C_SendConfigDataMessage.CODEC, (msg, ctx) -> msg.receive());
    }

    @Override
    public void dispatchClientMessage(ServerPlayer player, CustomPacketPayload message) {
        PacketDistributor.sendToPlayer(player, message);
    }

    private NeoforgeNetworkManager() {}
}
