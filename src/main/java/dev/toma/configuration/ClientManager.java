package dev.toma.configuration;

import dev.toma.configuration.api.type.ObjectType;
import dev.toma.configuration.client.screen.ConfigScreen;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;

import java.util.Map;
import java.util.Optional;

public class ClientManager {

    public static boolean setupModExtensions() {
        for (Map.Entry<String, ObjectType> entry : Configuration.configMap.entrySet()) {
            String modid = entry.getKey();
            ObjectType type = entry.getValue();
            Optional<? extends ModContainer> container = ModList.get().getModContainerById(modid);
            container.ifPresent(mc -> mc.registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (minecraft, screen) -> new ConfigScreen(screen, type, modid)));
        }
        return true;
    }
}
