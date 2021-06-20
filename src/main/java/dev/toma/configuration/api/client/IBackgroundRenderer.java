package dev.toma.configuration.api.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.configuration.api.client.screen.WidgetScreen;
import dev.toma.configuration.api.client.widget.ITickable;
import net.minecraft.client.Minecraft;

public interface IBackgroundRenderer extends ITickable {

    void drawBackground(Minecraft mc, MatrixStack stack, int mouseX, int mouseY, float partialTicks, WidgetScreen<?> screen);

    void drawHeaderBackground(Minecraft mc, MatrixStack stack, int x, int y, int headerWidth, int headerHeight, int mouseX, int mouseY, float partialTicks, WidgetScreen<?> screen);

    void drawFooterBackground(Minecraft mc, MatrixStack stack, int x, int y, int footerWidth, int footerHeight, int mouseX, int mouseY, float partialTicks, WidgetScreen<?> screen);

    void drawScrollbar(Minecraft mc, MatrixStack stack, int index, int displayAmount, int totalCount);

    int getDefaultLabelForegroundColor();

    int getTitleColor();
}
