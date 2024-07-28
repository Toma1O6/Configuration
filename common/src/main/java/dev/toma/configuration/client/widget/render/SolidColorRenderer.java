package dev.toma.configuration.client.widget.render;

import net.minecraft.client.gui.GuiGraphics;

import java.util.function.IntSupplier;

public class SolidColorRenderer implements IRenderer {

    private final IntSupplier colorProvider;

    public SolidColorRenderer(IntSupplier colorProvider) {
        this.colorProvider = colorProvider;
    }

    @Override
    public void draw(GuiGraphics graphics, int x, int y, int width, int height, boolean hovered) {
        graphics.fill(x, y, x + width, y + height, colorProvider.getAsInt());
    }
}
