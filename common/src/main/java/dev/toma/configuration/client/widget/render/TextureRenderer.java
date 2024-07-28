package dev.toma.configuration.client.widget.render;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class TextureRenderer implements IRenderer {

    private final ResourceLocation texture;
    private final int xOffset;
    private final int yOffset;
    private final int textureWidth;
    private final int textureHeight;

    public TextureRenderer(ResourceLocation texture, int xOffset, int yOffset, int textureWidth, int textureHeight) {
        this.texture = texture;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public void draw(GuiGraphics graphics, int x, int y, int width, int height, boolean hovered) {
        graphics.blit(this.texture, x + this.xOffset, y + this.yOffset, 0, 0.0F, 0.0F, this.textureWidth, this.textureHeight, this.textureWidth, this.textureHeight);
    }
}
