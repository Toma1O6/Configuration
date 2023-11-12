package dev.toma.configuration.network;

import dev.toma.configuration.Configuration;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public final class Networking {

    public static final Marker MARKER = MarkerManager.getMarker("Network");
    private static final int NETWORK_VERSION = 2;
    private static final SimpleChannel CHANNEL = ChannelBuilder
            .named(new ResourceLocation(Configuration.MODID, "network_channel"))
            .networkProtocolVersion(NETWORK_VERSION)
            .clientAcceptedVersions((status, version) -> version == NETWORK_VERSION)
            .serverAcceptedVersions((status, version) -> version == NETWORK_VERSION)
            .simpleChannel();

    public static void sendClientPacket(ServerPlayer target, IPacket<?> packet) {
        CHANNEL.send(packet, PacketDistributor.PLAYER.with(target));
    }

    public static final class PacketRegistry {

        public static void register() {
            registerNetworkPacket(S2C_SendConfigData.class);
        }

        private static <P extends IPacket<P>> void registerNetworkPacket(Class<P> packetType) {
            P packet;
            try {
                packet = packetType.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ReportedException(CrashReport.forThrowable(e, "Couldn't instantiate packet for registration. Make sure you have provided public constructor with no parameters."));
            }
            CHANNEL.messageBuilder(packetType)
                    .encoder(IPacket::encode)
                    .decoder(packet::decode)
                    .consumerMainThread(IPacket::handle)
                    .add();
        }
    }
}
