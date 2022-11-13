package dev.toma.configuration.network.api;

import net.minecraft.network.FriendlyByteBuf;

@FunctionalInterface
public interface IPacketDecoder<T> {

    T decode(FriendlyByteBuf buffer);
}
