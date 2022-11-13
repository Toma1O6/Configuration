package dev.toma.configuration.client;

import net.minecraft.client.gui.components.AbstractWidget;

public interface WidgetAdder extends IValidationHandler {

    <W extends AbstractWidget> W addConfigWidget(ToWidgetFunction<W> function);

    @FunctionalInterface
    interface ToWidgetFunction<W extends AbstractWidget> {

        W asWidget(int x, int y, int width, int height, String configId);
    }
}
