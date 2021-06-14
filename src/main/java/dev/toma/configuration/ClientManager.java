package dev.toma.configuration;

import dev.toma.configuration.api.IConfigPlugin;
import dev.toma.configuration.api.ModConfig;
import dev.toma.configuration.api.client.ClientHandles;
import dev.toma.configuration.api.client.screen.ComponentScreen;
import dev.toma.configuration.api.type.CollectionType;
import dev.toma.configuration.api.IConfigType;
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
        Optional<ModConfig> optional = Configuration.getConfig(parentScreen.getModID());
        optional.ifPresent(config -> {
            ClientHandles handles = config.getPlugin().getClientHandles();
            mc.displayGuiScreen(handles.createConfigScreen(parentScreen, type, parentScreen));
        });
    }

    public static <T extends IConfigType<?>> void displayCollectionScreen(ComponentScreen parentScreen, CollectionType<T> type) {
        Minecraft mc = Minecraft.getInstance();
        Optional<ModConfig> optional = Configuration.getConfig(parentScreen.getModID());
        optional.ifPresent(config -> {
            ClientHandles handles = config.getPlugin().getClientHandles();
            mc.displayGuiScreen(handles.createCollectionScreen(parentScreen, type, parentScreen));
        });
    }

    public static void enableConfigButton(ModConfig config) {
        IConfigPlugin plugin = config.getPlugin();
        ClientHandles handles = plugin.getClientHandles();
        ModList list = ModList.get();
        Optional<? extends ModContainer> optionalModContainer = list.getModContainerById(plugin.getModID());
        optionalModContainer.ifPresent(container -> container.registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, screen) -> handles.createConfigScreen(screen, config, plugin)));
    }
}
