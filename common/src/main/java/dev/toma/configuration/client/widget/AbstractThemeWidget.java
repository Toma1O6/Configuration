package dev.toma.configuration.client.widget;

import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.render.IRenderer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public abstract class AbstractThemeWidget extends AbstractWidget implements ThemeWidget {

    public static final Component REVERT = Component.translatable("text.configuration.value.revert");
    public static final Component REVERT_DEFAULT = Component.translatable("text.configuration.value.revert_default");

    protected final ConfigTheme theme;

    protected IRenderer backgroundRenderer;
    protected ChangeListener<AbstractThemeWidget> changeListener;

    public AbstractThemeWidget(int x, int y, int width, int height, ConfigTheme theme) {
        this(x, y, width, height, CommonComponents.EMPTY, theme);
    }

    public AbstractThemeWidget(int x, int y, int width, int height, Component text, ConfigTheme theme) {
        super(x, y, width, height, text);
        this.theme = theme;
    }

    @Override
    public void setBackgroundRenderer(IRenderer backgroundRenderer) {
        this.backgroundRenderer = backgroundRenderer;
    }

    public void renderBackground(GuiGraphicsExtractor graphics) {
        this.applyRenderer(this.backgroundRenderer, graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    public void applyRenderer(IRenderer renderer, GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        if (renderer != null) {
            renderer.draw(graphics, x, y, width, height, this.isHovered);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends AbstractWidget & ThemeWidget> void setChangeListener(ChangeListener<T> changeListener) {
        this.changeListener = (ChangeListener<AbstractThemeWidget>) changeListener;
    }

    @Override
    public void setChanged() {
        if (this.changeListener != null) {
            this.changeListener.onChanged(this);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, this.getMessage());
    }

    @Override
    public ConfigTheme getTheme() {
        return theme;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
}
