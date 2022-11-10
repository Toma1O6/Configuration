package dev.toma.configuration.client;

import dev.toma.configuration.config.validate.ValidationResult;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public interface WidgetAdder extends IValidationHandler {

    <W extends Widget> W addConfigWidget(ToWidgetFunction<W> function);

    @FunctionalInterface
    interface ToWidgetFunction<W extends Widget> {

        W asWidget(int x, int y, int width, int height, String configId);
    }
}
