package dev.toma.configuration.api.client.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.api.type.ColorType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

public class ColorDisplayComponent extends ConfigComponent<ColorType> {

    public static final ResourceLocation COLOR_BACKGROUND = new ResourceLocation(Configuration.MODID, "textures/background_empty.png");
    int actualColor;

    public ColorDisplayComponent(ColorType type, int x, int y, int width, int height) {
        super(type, x, y, width, height);
        this.actualColor = type.getColor();
    }

    @Override
    public void drawComponent(MatrixStack matrixStack, FontRenderer font, int mouseX, int mouseY, float partialTicks, boolean hovered) {
        float a = ((actualColor >> 24) & 255) / 255.0F;
        if(a == 0.0F) a = 1.0F;
        float r = ((actualColor >> 16) & 255) / 255.0F;
        float g = ((actualColor >>  8) & 255) / 255.0F;
        float b = ( actualColor        & 255) / 255.0F;
        drawColorShape(matrixStack, x, y, x + width, y + height, 1.0F, 1.0F, 1.0F, 1.0F);
        if(a < 1.0F) {
            Minecraft.getInstance().getTextureManager().bindTexture(COLOR_BACKGROUND);
            drawTexturedShape(matrixStack, x + 1, y + 1, x + width - 1, y + height - 1);
        }
        drawColorShape(matrixStack, x + 1, y + 1, x + width - 1, y + height - 1, r, g, b, a);
    }

    @Override
    public void onUpdate() {
        actualColor = configType.getColor();
    }
}
