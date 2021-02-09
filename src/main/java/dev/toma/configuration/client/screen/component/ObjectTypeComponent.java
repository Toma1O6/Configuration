package dev.toma.configuration.client.screen.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.configuration.api.type.ObjectType;
import dev.toma.configuration.client.screen.ComponentScreen;
import dev.toma.configuration.client.screen.ConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ObjectTypeComponent extends ConfigComponent<ObjectType> {

    final ComponentScreen parentScreen;

    public ObjectTypeComponent(ComponentScreen screen, ObjectType type, int x, int y, int width, int height) {
        super(type, x, y, width, height);
        this.parentScreen = screen;
    }

    @Override
    public void drawComponent(MatrixStack matrixStack, FontRenderer font, int mouseX, int mouseY, float partialTicks, boolean hovered) {
        drawColorShape(matrixStack, x, y, x + width, y + height, 1.0F, 1.0F, 1.0F, 1.0F);
        drawColorShape(matrixStack, x + 1, y + 1, x + width - 1, y + height - 1, 0.0F, 0.0F, 0.0F, 1.0F);
        int tw = font.getStringWidth(configType.getId());
        font.drawStringWithShadow(matrixStack, configType.getId(), x + (width - tw) / 2.0F, y + (height - font.FONT_HEIGHT) / 2.0F, hovered ? 0xFFFF00 : 0xFFFFFF);
    }

    @Override
    public void processClicked(double mouseX, double mouseY) {
        Minecraft.getInstance().displayGuiScreen(new ConfigScreen(parentScreen, configType, parentScreen.getModID()));
    }
}
