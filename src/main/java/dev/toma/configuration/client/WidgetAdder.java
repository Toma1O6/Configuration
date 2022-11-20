package dev.toma.configuration.client;

import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;

public interface WidgetAdder extends IValidationHandler {

    <W extends Widget> W addConfigWidget(ToWidgetFunction<W> function);

    ITextComponent getComponentName();

    @FunctionalInterface
    interface ToWidgetFunction<W extends Widget> {

        W asWidget(int x, int y, int width, int height, String configId);
    }
}
