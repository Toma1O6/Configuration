package dev.toma.configuration.mixin;

import dev.toma.configuration.config.io.ConfigIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin extends ClientCommonPacketListenerImpl implements ClientGamePacketListener, TickablePacketListener {

    public ClientPacketListenerMixin(Minecraft minecraft, Connection connection, CommonListenerCookie cookie) {
        super(minecraft, connection, cookie);
    }

    @Inject(
            method = "handleLogin",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;addEntity(Lnet/minecraft/world/entity/Entity;)V", shift = At.Shift.BEFORE)
    )
    private void configuration$handleLogin(CallbackInfo ci) {
        // Set current environment to playing to disallow modification of synchronized/menu only edit fields on dedicated servers
        ConfigIO.setEnvironment(ConfigIO.ConfigEnvironment.PLAYING);
    }
}
