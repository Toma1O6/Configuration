package dev.toma.configuration.client.widget.render;

import net.minecraft.client.gui.GuiGraphics;

public interface IRenderer {

    void draw(GuiGraphics graphics, int x, int y, int width, int height, boolean hovered);
}
