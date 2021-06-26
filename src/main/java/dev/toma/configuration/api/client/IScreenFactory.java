package dev.toma.configuration.api.client;

import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.client.screen.WidgetScreen;
import net.minecraft.client.gui.screen.Screen;

/**
 * Config screen factory.
 * @param <T> Config type implementation
 */
public interface IScreenFactory<T extends IConfigType<?>> {

    /**
     * Creates new screen for supplied config type
     * @param parent Parent screen
     * @param type Config type (container)
     * @param context Screen opening context containing plugin data
     * @return New instance of config screen
     */
    WidgetScreen<T> createScreen(Screen parent, T type, ScreenOpenContext context);
}
