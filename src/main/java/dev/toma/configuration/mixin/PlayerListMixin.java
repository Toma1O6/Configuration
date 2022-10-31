package dev.toma.configuration.mixin;

import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.network.Networking;
import dev.toma.configuration.network.S2C_SendConfigData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.management.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    private void configuration_sendServerConfigs(NetworkManager manager, ServerPlayerEntity player, CallbackInfo ci) {
        Set<String> set = ConfigHolder.getSynchronizedConfigs();
        set.forEach(id -> Networking.sendClientPacket(player, new S2C_SendConfigData(id)));
    }
}
