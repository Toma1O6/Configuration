package dev.toma.configuration.client.theme.adapter;

import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.screen.ConfigScreen;
import dev.toma.configuration.client.screen.WidgetPlacerHelper;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.ConfigEntryWidget;
import dev.toma.configuration.client.widget.ThemedButtonWidget;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.ObjectValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.lang.reflect.Field;
import java.util.Map;

public class ObjectDisplayAdapter extends AbstractDisplayAdapter {

    @Override
    public void placeWidgets(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        ObjectValue objectValue = (ObjectValue) value;
        Map<String, ConfigValue<?>> map = objectValue.get();
        ThemedButtonWidget button = container.addConfigWidget((x, y, width, height, configId) -> {
            int left = WidgetPlacerHelper.getLeft(x, width);
            int widgetWidth = WidgetPlacerHelper.getWidth(width);
            return new ThemedButtonWidget(left, y, widgetWidth, height, ConfigEntryWidget.OPEN, theme);
        });
        button.setBackgroundRenderer(theme.getButtonBackground(button));
        button.setClickListener((widget, mouseX, mouseY) -> {
            Minecraft client = Minecraft.getInstance();
            Screen currentScreen = client.screen;
            Screen nestedConfigScreen = new ConfigScreen(holder, container.getComponentName(), map, currentScreen);
            client.setScreen(nestedConfigScreen);
        });

        ValueReverter reverter = def -> {
            if (def)
                objectValue.revertChangesToDefault();
            else
                objectValue.revertChanges();
            button.setChanged();
        };

        createControls(button, objectValue, theme, container, reverter);
    }
}
