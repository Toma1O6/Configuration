package dev.toma.configuration.client.widget.render;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

public class SpriteRenderer implements IRenderer {

    private final Supplier<Identifier> resourceProvider;

    public SpriteRenderer(Supplier<Identifier> resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    @Override
    public void draw(GuiGraphics graphics, int x, int y, int width, int height, boolean hovered) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, resourceProvider.get(), x, y, width, height);
    }
}
