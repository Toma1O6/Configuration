package dev.toma.configuration.client.screen.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.configuration.api.type.BooleanType;
import net.minecraft.client.gui.FontRenderer;

import java.util.Objects;

public class BooleanComponent extends ConfigComponent<BooleanType> {

    public BooleanComponent(BooleanType type, int x, int y, int width, int height) {
        super(type, x, y, width, height);
    }

    @Override
    public void processClicked(double mouseX, double mouseY) {
        configType.set(!configType.get());
    }

    @Override
    public void drawComponent(MatrixStack matrixStack, FontRenderer font, int mouseX, int mouseY, float partialTicks, boolean hovered) {
        drawColorShape(matrixStack, x, y, x + width, y + height, 1.0F, 1.0F, 1.0F, 1.0F);
        drawColorShape(matrixStack, x + 1, y + 1, x + width - 1, y + height - 1, 0.0F, 0.0F, 0.0F, 1.0F);
        boolean value = configType.get();
        String text = Objects.toString(value);
        int w = font.getStringWidth(text);
        font.drawStringWithShadow(matrixStack, text, x + (width - w) / 2.0F, y + (height - font.FONT_HEIGHT) / 2.0F, value ? 0x00AA00 : 0xAA0000);
    }
}
