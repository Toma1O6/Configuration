package dev.toma.configuration.network;

import dev.toma.configuration.Configuration;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public final class Networking {

    public static final Marker MARKER = MarkerManager.getMarker("Network");
    private static final String NETWORK_VERSION = "2.0.0";
    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(Configuration.MODID, "network_channel"))
            .networkProtocolVersion(() -> NETWORK_VERSION)
            .clientAcceptedVersions(NETWORK_VERSION::equals)
            .serverAcceptedVersions(NETWORK_VERSION::equals)
            .simpleChannel();

    public static void sendClientPacket(ServerPlayerEntity target, IPacket<?> packet) {
        CHANNEL.sendTo(packet, target.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static final class PacketRegistry {

        private static int packetIndex;

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
            CHANNEL.registerMessage(packetIndex++, packetType, IPacket::encode, packet::decode, IPacket::handle);
        }
    }
}
