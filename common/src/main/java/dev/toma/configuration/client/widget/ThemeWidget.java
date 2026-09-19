package dev.toma.configuration.client.widget;

import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.render.IRenderer;
import net.minecraft.client.gui.components.AbstractWidget;

public interface ThemeWidget {

    void setBackgroundRenderer(IRenderer renderer);

    <T extends AbstractWidget & ThemeWidget> void setChangeListener(ChangeListener<T> listener);

    void setChanged();

    ConfigTheme getTheme();

    int getX();

    int getY();

    int getRight();

    int getBottom();

    int getWidth();

    int getHeight();

    void setActive(boolean active);

    boolean isActive();

    boolean isHoveredOrFocused();

    @FunctionalInterface
    interface ChangeListener<T> {
        void onChanged(T value);
    }
}
