package dev.toma.configuration.client.widget.render;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface IRenderer {

    void draw(GuiGraphicsExtractor graphics, int x, int y, int width, int height, boolean hovered);
}
