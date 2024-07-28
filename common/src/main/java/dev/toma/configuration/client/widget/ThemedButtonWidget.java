package dev.toma.configuration.client.widget;

import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.render.IRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class ThemedButtonWidget extends AbstractThemeWidget {

    protected IRenderer foregroundRenderer;

    private ClickListener clickListener;

    public ThemedButtonWidget(int x, int y, int width, int height, Component label, ConfigTheme theme) {
        super(x, y, width, height, label, theme);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if (this.backgroundRenderer != null) {
            this.backgroundRenderer.draw(guiGraphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.isHovered);
        }
        this.renderScrollingString(guiGraphics, Minecraft.getInstance().font, 2, this.theme.getWidgetTextColor(this.active, this.isHovered));
        this.applyRenderer(this.foregroundRenderer, guiGraphics, this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    public void setForegroundRenderer(IRenderer foregroundRenderer) {
        this.foregroundRenderer = foregroundRenderer;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (this.clickListener != null) {
            this.clickListener.onClick(this, mouseX, mouseY);
        }
    }

    @FunctionalInterface
    public interface ClickListener {
        void onClick(ThemedButtonWidget widget, double mouseX, double mouseY);
    }
}
