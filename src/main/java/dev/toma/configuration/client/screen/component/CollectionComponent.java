package dev.toma.configuration.client.screen.component;

import dev.toma.configuration.api.type.AbstractConfigType;
import dev.toma.configuration.api.type.CollectionType;
import dev.toma.configuration.client.screen.CollectionScreen;
import dev.toma.configuration.client.screen.ComponentScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class CollectionComponent<T extends AbstractConfigType<?>> extends ConfigComponent<CollectionType<T>> {

    final ComponentScreen parentScreen;

    public CollectionComponent(ComponentScreen parentScreen, CollectionType<T> type, int x, int y, int width, int height) {
        super(type, x, y, width, height);
        this.parentScreen = parentScreen;
    }

    @Override
    public void processClicked(double mouseX, double mouseY) {
        Minecraft.getInstance().displayGuiScreen(new CollectionScreen<>(parentScreen, configType, parentScreen.getModID()));
    }

    @Override
    public void drawComponent(FontRenderer font, int mouseX, int mouseY, float partialTicks, boolean hovered) {
        drawColorShape(x, y, x + width, y + height, 1.0F, 1.0F, 1.0F, 1.0F);
        drawColorShape(x + 1, y + 1, x + width - 1, y + height - 1, 0.0F, 0.0F, 0.0F, 1.0F);
        int tw = font.getStringWidth(configType.getId());
        font.drawStringWithShadow(configType.getId(), x + (width - tw) / 2.0F, y + (height - font.FONT_HEIGHT) / 2.0F, hovered ? 0xFFFF00 : 0xFFFFFF);
    }
}
