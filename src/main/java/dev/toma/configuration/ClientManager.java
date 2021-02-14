package dev.toma.configuration;

import dev.toma.configuration.api.ConfigPlugin;
import dev.toma.configuration.api.client.ClientHandles;
import dev.toma.configuration.api.type.ObjectType;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;

import java.util.Map;
import java.util.Optional;

public class ClientManager {

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
