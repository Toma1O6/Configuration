package dev.toma.configuration.client.widget.render;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class SpriteBackgroundRenderer implements IBackgroundRenderer {

    private final Supplier<ResourceLocation> resourceProvider;

    public SpriteBackgroundRenderer(Supplier<ResourceLocation> resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    @Override
    public void draw(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.blitSprite(resourceProvider.get(), x, y, width, height);
    }
}
