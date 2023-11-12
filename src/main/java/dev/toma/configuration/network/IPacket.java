package dev.toma.configuration.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public interface IPacket<P extends IPacket<P>> {

    void encode(FriendlyByteBuf buffer);

    P decode(FriendlyByteBuf buffer);

    void handle(CustomPayloadEvent.Context context);
}
