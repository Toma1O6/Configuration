package dev.toma.configuration.api;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.api.client.ClientHandles;
import dev.toma.configuration.api.client.screen.ComponentScreen;
import dev.toma.configuration.api.type.AbstractConfigType;
import dev.toma.configuration.api.type.CollectionType;
import dev.toma.configuration.api.type.ObjectType;
import net.minecraft.client.Minecraft;

import java.util.Optional;

public class ConfigurationClient {

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
}
