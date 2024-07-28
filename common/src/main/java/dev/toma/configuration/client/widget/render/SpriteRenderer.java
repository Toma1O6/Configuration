package dev.toma.configuration.client.widget.render;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class SpriteRenderer implements IRenderer {

    private final Supplier<ResourceLocation> resourceProvider;

    public SpriteRenderer(Supplier<ResourceLocation> resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    @Override
    public void draw(GuiGraphics graphics, int x, int y, int width, int height, boolean hovered) {
        graphics.blitSprite(resourceProvider.get(), x, y, width, height);
    }
}
