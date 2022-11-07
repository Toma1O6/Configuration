package dev.toma.configuration.client;

import dev.toma.configuration.config.value.ConfigValue;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public interface WidgetAdder {

    ConfigValue<?> getConfigValue();

    void addConfigWidget(ToWidgetFunction function);

    void setError(@Nullable ITextComponent error);

    int elementIndex();

    @FunctionalInterface
    interface ToWidgetFunction {

        Widget asWidget(int x, int y, int width, int height, String configId);
    }
}
