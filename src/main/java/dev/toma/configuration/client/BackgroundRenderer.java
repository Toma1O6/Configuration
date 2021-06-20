package dev.toma.configuration.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.configuration.api.client.IBackgroundRenderer;
import dev.toma.configuration.api.client.screen.WidgetScreen;
import dev.toma.configuration.api.client.widget.Widget;
import net.minecraft.client.Minecraft;

public class BackgroundRenderer implements IBackgroundRenderer {

    @Override
    public void drawBackground(Minecraft mc, MatrixStack stack, int mouseX, int mouseY, float partialTicks, WidgetScreen<?> screen) {
        screen.renderDirtBackground(0);
    }

    @Override
    public void drawHeaderBackground(Minecraft mc, MatrixStack stack, int x, int y, int headerWidth, int headerHeight, int mouseX, int mouseY, float partialTicks, WidgetScreen<?> screen) {
        int bottom = y + headerHeight;
        Widget.drawColorShape(stack, x, y, x + headerWidth, bottom, 0.0F, 0.0F, 0.0F, 0.3F);
        Widget.drawColorShape(stack, x, bottom - 1, x + headerWidth, bottom, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void drawFooterBackground(Minecraft mc, MatrixStack stack, int x, int y, int footerWidth, int footerHeight, int mouseX, int mouseY, float partialTicks, WidgetScreen<?> screen) {
        Widget.drawColorShape(stack, x, y, x + footerWidth, y + footerHeight, 0.0F, 0.0F, 0.0F, 0.3F);
        Widget.drawColorShape(stack, x, y, x + footerWidth, y + 1, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void drawScrollbar(Minecraft mc, MatrixStack stack, int index, int displayAmount, int totalCount) {

    }

    @Override
    public int getDefaultLabelForegroundColor() {
        return 0xFFFFFF;
    }

    @Override
    public int getTitleColor() {
        return getDefaultLabelForegroundColor();
    }

    @Override
    public void tick() {
    }
}
