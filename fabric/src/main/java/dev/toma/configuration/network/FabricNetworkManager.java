package dev.toma.configuration.network;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.network.message.NetworkMessage;
import dev.toma.configuration.network.message.S2C_SendConfigDataMessage;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class FabricNetworkManager implements NetworkManager {

    public static final FabricNetworkManager INSTANCE = new FabricNetworkManager();

    public void registerMessages() {
        if (Configuration.PLATFORM.getEnvironment() == dev.toma.configuration.config.Environment.CLIENT) {
            this.registerClientReceivers();
        }
    }

    @Override
    public void dispatchClientMessage(ServerPlayer player, NetworkMessage message) {
        dispatch(message, (resourceLocation, buf) -> ServerPlayNetworking.send(player, resourceLocation, buf));
    }

    @Environment(EnvType.CLIENT)
    private void registerClientReceivers() {
        this.registerMessageS2C(S2C_SendConfigDataMessage.IDENTIFIER, S2C_SendConfigDataMessage::read);
    }

    @Environment(EnvType.CLIENT)
    private <T extends NetworkMessage> void registerMessageS2C(ResourceLocation pid, Function<FriendlyByteBuf, T> constructor) {
        ClientPlayNetworking.registerGlobalReceiver(pid, (client, handler, buf, responseSender) -> {
            T message = constructor.apply(buf);
            client.execute(() -> handlePayload(message, client));
        });
    }

    @Environment(EnvType.CLIENT)
    private static <T extends NetworkMessage> void handlePayload(T message, Minecraft client) {
        message.handle(client.player);
    }

    private static <T extends NetworkMessage> void dispatch(T message, BiConsumer<ResourceLocation, FriendlyByteBuf> dispatcher) {
        ResourceLocation pid = message.getPacketId();
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        message.write(buf);
        dispatcher.accept(pid, buf);
    }

    private FabricNetworkManager() {}
}
