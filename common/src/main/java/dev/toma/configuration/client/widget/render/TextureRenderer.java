package dev.toma.configuration.client.widget.render;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class TextureRenderer implements IRenderer {

    private final Identifier texture;
    private final int xOffset;
    private final int yOffset;
    private final int textureWidth;
    private final int textureHeight;

    public TextureRenderer(Identifier texture, int xOffset, int yOffset, int textureWidth, int textureHeight) {
        this.texture = texture;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public void draw(GuiGraphicsExtractor graphics, int x, int y, int width, int height, boolean hovered) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, this.texture, x + this.xOffset, y + this.yOffset, 0.0F, 0.0F, this.textureWidth, this.textureHeight, this.textureWidth, this.textureHeight);
    }
}
