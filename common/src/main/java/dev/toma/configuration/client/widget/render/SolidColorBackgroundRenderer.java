package dev.toma.configuration.client.widget.render;

import net.minecraft.client.gui.GuiGraphics;

import java.util.function.IntSupplier;

public class SolidColorBackgroundRenderer implements IBackgroundRenderer {

    private final IntSupplier colorProvider;

    public SolidColorBackgroundRenderer(IntSupplier colorProvider) {
        this.colorProvider = colorProvider;
    }

    @Override
    public void draw(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, colorProvider.getAsInt());
    }
}
