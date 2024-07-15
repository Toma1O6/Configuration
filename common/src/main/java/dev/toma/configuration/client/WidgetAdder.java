package dev.toma.configuration.client;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

public interface WidgetAdder extends IValidationHandler {

    <W extends AbstractWidget> W addConfigWidget(boolean editableCheck, ToWidgetFunction<W> function);

    default <W extends AbstractWidget> W addConfigWidget(ToWidgetFunction<W> function) {
        return addConfigWidget(true, function);
    }

    Component getComponentName();

    @FunctionalInterface
    interface ToWidgetFunction<W extends AbstractWidget> {

        W asWidget(int x, int y, int width, int height, String configId);
    }
}
