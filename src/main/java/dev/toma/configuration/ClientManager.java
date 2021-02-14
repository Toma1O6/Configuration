package dev.toma.configuration;

import dev.toma.configuration.api.ConfigPlugin;
import dev.toma.configuration.api.client.ClientHandles;
import dev.toma.configuration.api.client.screen.ComponentScreen;
import dev.toma.configuration.api.type.AbstractConfigType;
import dev.toma.configuration.api.type.CollectionType;
import dev.toma.configuration.api.type.ObjectType;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;

import java.util.Map;
import java.util.Optional;

public class ClientManager {

    public static void displayObjectScreen(ComponentScreen parentScreen, ObjectType type) {
        Minecraft mc = Minecraft.getInstance();
        Optional<ConfigPlugin> optional = Configuration.getPlugin(parentScreen.getModID());
        optional.ifPresent(plugin -> {
            ClientHandles handles = plugin.getClientHandles();
            mc.displayGuiScreen(handles.createConfigScreen(parentScreen, type, parentScreen));
        });
    }

    public static <T extends AbstractConfigType<?>> void displayCollectionScreen(ComponentScreen parentScreen, CollectionType<T> type) {
        Minecraft mc = Minecraft.getInstance();
        Optional<ConfigPlugin> optional = Configuration.getPlugin(parentScreen.getModID());
        optional.ifPresent(plugin -> {
            ClientHandles handles = plugin.getClientHandles();
            mc.displayGuiScreen(handles.createCollectionScreen(parentScreen, type, parentScreen));
        });
    }

    public static boolean setupPluginClient() {
        for (Map.Entry<String, ObjectType> entry : Configuration.configMap.entrySet()) {
            String modid = entry.getKey();
            ObjectType type = entry.getValue();
            Optional<ConfigPlugin> optional = Configuration.getPlugin(modid);
            optional.ifPresent(plugin -> {
                ClientHandles handles = plugin.getClientHandles();
                Optional<? extends ModContainer> container = ModList.get().getModContainerById(modid);
                container.ifPresent(mc -> mc.registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (minecraft, screen) -> handles.createConfigScreen(screen, type, plugin)));
            });
        }
        return true;
    }
}
