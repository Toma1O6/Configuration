package dev.toma.configuration.api.client.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.configuration.ClientManager;
import dev.toma.configuration.api.client.screen.ComponentScreen;
import dev.toma.configuration.api.type.CollectionType;
import dev.toma.configuration.api.IConfigType;
import net.minecraft.client.gui.FontRenderer;

public class CollectionComponent<T extends IConfigType<?>> extends ConfigComponent<CollectionType<T>> {

    final ComponentScreen parentScreen;

    public CollectionComponent(ComponentScreen parentScreen, CollectionType<T> type, int x, int y, int width, int height) {
        super(type, x, y, width, height);
        this.parentScreen = parentScreen;
    }

    @Override
    public void processClicked(double mouseX, double mouseY) {
        ClientManager.displayCollectionScreen(parentScreen, configType);
    }

    @Override
    public void drawComponent(MatrixStack matrixStack, FontRenderer font, int mouseX, int mouseY, float partialTicks, boolean hovered) {
        drawColorShape(matrixStack, x, y, x + width, y + height, 1.0F, 1.0F, 1.0F, 1.0F);
        drawColorShape(matrixStack, x + 1, y + 1, x + width - 1, y + height - 1, 0.0F, 0.0F, 0.0F, 1.0F);
        int tw = font.getStringWidth(configType.getId());
        font.drawStringWithShadow(matrixStack, configType.getId(), x + (width - tw) / 2.0F, y + (height - font.FONT_HEIGHT) / 2.0F, hovered ? 0xFFFF00 : 0xFFFFFF);
    }
}
