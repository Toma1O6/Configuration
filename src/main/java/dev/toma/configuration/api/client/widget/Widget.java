package dev.toma.configuration.api.client.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.client.HorizontalAlignment;
import dev.toma.configuration.api.client.ScreenOpenContext;
import dev.toma.configuration.api.client.VerticalAlignment;
import dev.toma.configuration.api.client.screen.WidgetScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Matrix4f;

public abstract class Widget implements ITickable {

    private final WidgetType<?> type;
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected IConfigType<?> configType;
    public int foreground;
    public int background;
    public WidgetState visibilityState = WidgetState.VISIBLE;

    public Widget(WidgetType<?> type, IConfigType<?> configType, int x, int y, int width, int height) {
        this.type = type;
        this.configType = configType;
        resize(x, y, width, height);
    }

    public void resize(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void tick() {}

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    public boolean charTyped(char codePoint, int modifiers) {
        return false;
    }

    /**
     * Method called on widget container closing.
     * Utilized by key input widgets
     */
    public void save() {

    }

    public void playPressSound() {
        Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public void assignParent(WidgetScreen<?> screen) {
    }

    public WidgetType<?> getWidgetType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getX(int offset) {
        return x + offset;
    }

    public int getY() {
        return y;
    }

    public int getY(int offset) {
        return y + offset;
    }

    public int getWidth() {
        return width;
    }

    public int getWidth(int offset) {
        return width + offset;
    }

    public int getHeight() {
        return height;
    }

    public int getHeight(int offset) {
        return height + offset;
    }

    public static void drawColorShape(MatrixStack stack, int x1, int y1, int x2, int y2, int color) {
        float a = ((color >> 24) & 255) / 255.0F;
        float r = ((color >> 16) & 255) / 255.0F;
        float g = ((color >>  8) & 255) / 255.0F;
        float b = ( color        & 255) / 255.0F;
        drawColorShape(stack, x1, y1, x2, y2, r, g, b, a);
    }

    public static void drawColorShape(MatrixStack stack, int x1, int y1, int x2, int y2, float r, float g, float b, float a) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        Matrix4f matrix4f = stack.last().pose();
        BufferBuilder builder = Tessellator.getInstance().getBuilder();
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        builder.vertex(matrix4f, x1, y2, 0).color(r, g, b, a).endVertex();
        builder.vertex(matrix4f, x2, y2, 0).color(r, g, b, a).endVertex();
        builder.vertex(matrix4f, x2, y1, 0).color(r, g, b, a).endVertex();
        builder.vertex(matrix4f, x1, y1, 0).color(r, g, b, a).endVertex();
        builder.end();
        WorldVertexBufferUploader.end(builder);
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    public static void drawTexturedShape(MatrixStack stack, int x1, int y1, int x2, int y2) {
        RenderSystem.enableBlend();
        Matrix4f matrix4f = stack.last().pose();
        BufferBuilder builder = Tessellator.getInstance().getBuilder();
        builder.begin(7, DefaultVertexFormats.POSITION_TEX);
        builder.vertex(matrix4f, x1, y2, 0).uv(0, 1).endVertex();
        builder.vertex(matrix4f, x2, y2, 0).uv(1, 1).endVertex();
        builder.vertex(matrix4f, x2, y1, 0).uv(1, 0).endVertex();
        builder.vertex(matrix4f, x1, y1, 0).uv(0, 0).endVertex();
        builder.end();
        WorldVertexBufferUploader.end(builder);
        RenderSystem.disableBlend();
    }

    public static void drawCenteredString(String text, MatrixStack stack, FontRenderer renderer, int x, int y, int width, int height, int color) {
        drawAlignedString(text, stack, renderer, x, y, width, height, color, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
    }

    public static void drawAlignedString(String text, MatrixStack stack, FontRenderer renderer, int x, int y, int width, int height, int color, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment) {
        float left = horizontalAlignment.getHorizontalPos(x, width, renderer.width(text));
        float top = verticalAlignment.getVerticalPos(y, height, renderer.lineHeight);
        renderer.drawShadow(stack, text, left, top, color);
    }
}
