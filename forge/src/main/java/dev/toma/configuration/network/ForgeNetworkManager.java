package dev.toma.configuration.network;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.network.message.S2C_SendConfigDataMessage;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public class ForgeNetworkManager implements NetworkManager {

    public static final ForgeNetworkManager INSTANCE = new ForgeNetworkManager();
    private static final SimpleChannel CHANNEL = ChannelBuilder.named(ResourceLocation.fromNamespaceAndPath(Configuration.MODID, "network"))
            .networkProtocolVersion(1)
            .simpleChannel();

    public static void registerMessages() {
        CHANNEL.messageBuilder(S2C_SendConfigDataMessage.class)
                .codec(S2C_SendConfigDataMessage.CODEC)
                .consumerMainThread((s2CSendConfigDataMessage, context) -> s2CSendConfigDataMessage.receive())
                .add();
    }

    @Override
    public void dispatchClientMessage(ServerPlayer player, CustomPacketPayload message) {
        CHANNEL.send(message, PacketDistributor.PLAYER.with(player));
    }
}
