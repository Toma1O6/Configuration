package dev.toma.configuration.handler;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.network.Networking;
import dev.toma.configuration.network.S2C_SendConfigData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

@Mod.EventBusSubscriber(modid = Configuration.MODID)
public class LoginEventHandler {

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        Set<String> set = ConfigHolder.getSynchronizedConfigs();
        set.forEach(id -> Networking.sendClientPacket(player, new S2C_SendConfigData(id)));
    }
}
