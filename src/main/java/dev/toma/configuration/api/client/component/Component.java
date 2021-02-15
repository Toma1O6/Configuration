package dev.toma.configuration.api.client.component;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class Component {

    public int x, y, width, height;

    public Component(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void processClicked(double mouseX, double mouseY) {
    }

    public void keyPressed(int keyCode, int scanCode, int modifiers) {
    }

    public void charTyped(char character, int modifiers) {
    }

    public void mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
    }

    public void drawComponent(FontRenderer font, int mouseX, int mouseY, float partialTicks, boolean hovered) {
        drawColorShape(x, y, x + width, y + height, 0.0F, 0.0F, 0.0F, 0.0F);
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public boolean hasClicked(double mouseX, double mouseY, int button) {
        return button == 0;
    }

    public static void drawColorShape(int x1, int y1, int x2, int y2, float r, float g, float b, float a) {
        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        builder.pos(x1, y2, 0).color(r, g, b, a).endVertex();
        builder.pos(x2, y2, 0).color(r, g, b, a).endVertex();
        builder.pos(x2, y1, 0).color(r, g, b, a).endVertex();
        builder.pos(x1, y1, 0).color(r, g, b, a).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture();
    }

    public static void drawTexturedShape(int x1, int y1, int x2, int y2) {
        GlStateManager.enableBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_TEX);
        builder.pos(x1, y2, 0).tex(0, 1).endVertex();
        builder.pos(x2, y2, 0).tex(1, 1).endVertex();
        builder.pos(x2, y1, 0).tex(1, 0).endVertex();
        builder.pos(x1, y1, 0).tex(0, 0).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
    }
}
