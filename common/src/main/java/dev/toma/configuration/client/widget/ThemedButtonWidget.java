package dev.toma.configuration.client.widget;

import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.render.IRenderer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class ThemedButtonWidget extends AbstractThemeWidget {

    protected IRenderer foregroundRenderer;

    private ClickListener clickListener;

    public ThemedButtonWidget(int x, int y, int width, int height, Component label, ConfigTheme theme) {
        super(x, y, width, height, label, theme);
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (this.backgroundRenderer != null) {
            this.backgroundRenderer.draw(graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.isHovered);
        }
        ConfigurationRenderUtils.renderCenteredScrollingString(this, this.getMessage(), this.theme.getWidgetTextColor(this.active, this.isHovered), 2, graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
        this.applyRenderer(this.foregroundRenderer, graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    public void setForegroundRenderer(IRenderer foregroundRenderer) {
        this.foregroundRenderer = foregroundRenderer;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        if (this.clickListener != null) {
            this.clickListener.onClick(this, event.x(), event.y());
        }
    }

    @FunctionalInterface
    public interface ClickListener {
        void onClick(ThemedButtonWidget widget, double mouseX, double mouseY);
    }
}
