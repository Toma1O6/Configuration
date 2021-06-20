package dev.toma.configuration.api.client;

import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.client.screen.WidgetScreen;
import net.minecraft.client.gui.screen.Screen;

public interface IScreenFactory<T extends IConfigType<?>> {

    WidgetScreen<T> createScreen(Screen parent, T type, ScreenOpenContext context);
}
