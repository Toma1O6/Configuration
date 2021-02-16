package dev.toma.configuration.internal.proxy;

import dev.toma.configuration.api.client.screen.ComponentScreen;
import dev.toma.configuration.internal.gui.ConfigGui;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.client.GuiModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.START) {
            Minecraft mc = Minecraft.getMinecraft();
            if(mc.currentScreen instanceof ComponentScreen) {
                ((ComponentScreen) mc.currentScreen).tick();
            }
        }
    }

    @SubscribeEvent
    public static void replaceConfigGui(GuiOpenEvent event) {
        if(event.getGui() instanceof GuiModList) {
            event.setGui(new ConfigGui(null));
        }
    }
}
