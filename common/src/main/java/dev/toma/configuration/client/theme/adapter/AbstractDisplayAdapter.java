package dev.toma.configuration.client.theme.adapter;

import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.screen.AbstractConfigScreen;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.AbstractThemeWidget;
import dev.toma.configuration.client.widget.ThemeWidget;
import dev.toma.configuration.client.widget.ThemedButtonWidget;
import dev.toma.configuration.client.widget.render.TextureRenderer;
import dev.toma.configuration.config.value.ConfigValue;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;

import java.time.Duration;

public abstract class AbstractDisplayAdapter implements DisplayAdapter {

    protected void createControls(ThemeWidget widget, ConfigValue<?> value, ConfigTheme theme, WidgetAdder container, ValueReverter reverter) {
        ThemedButtonWidget revertButton = this.createRevertButton(widget, value, theme, container, reverter);
        ThemedButtonWidget revertDefaultButton = this.createRevertToDefaultButton(widget, value, theme, container, reverter);
        attachDefaultChangeListeners(value, widget, revertButton, revertDefaultButton);
    }

    protected ThemedButtonWidget createRevertButton(ThemeWidget linkedTo, ConfigValue<?> value, ConfigTheme theme, WidgetAdder adder, ValueReverter reverter) {
        return adder.addConfigWidget(false, (_, _, _, _, _) -> {
            int left = linkedTo.getRight() + 1;
            ThemedButtonWidget widget = new ThemedButtonWidget(left, linkedTo.getY(), 20, linkedTo.getHeight(), CommonComponents.EMPTY, linkedTo.getTheme());
            widget.setClickListener((_, _, _) -> reverter.revert(false));
            widget.setTooltip(Tooltip.create(AbstractThemeWidget.REVERT));
            widget.setTooltipDelay(Duration.ofMillis(200));
            widget.setBackgroundRenderer(theme.getButtonBackground(widget));
            widget.setForegroundRenderer(new TextureRenderer(AbstractConfigScreen.ICON_REVERT, 2, 2, 16, 16));
            widget.setActive(value.isEditable() && value.isChanged());
            return widget;
        });
    }

    protected ThemedButtonWidget createRevertToDefaultButton(ThemeWidget linkedTo, ConfigValue<?> value, ConfigTheme theme, WidgetAdder adder, ValueReverter reverter) {
        return adder.addConfigWidget(false, (_, _, _, _, _) -> {
            int left = linkedTo.getRight() + 22;
            ThemedButtonWidget widget = new ThemedButtonWidget(left, linkedTo.getY(), 20, linkedTo.getHeight(), CommonComponents.EMPTY, linkedTo.getTheme());
            widget.setClickListener((_, _, _) -> reverter.revert(true));
            widget.setTooltip(Tooltip.create(AbstractThemeWidget.REVERT_DEFAULT));
            widget.setTooltipDelay(Duration.ofMillis(200));
            widget.setBackgroundRenderer(theme.getButtonBackground(widget));
            widget.setForegroundRenderer(new TextureRenderer(AbstractConfigScreen.ICON_REVERT_DEFAULT, 2, 2, 16, 16));
            widget.setActive(value.isEditable() && value.isChangedFromDefault());
            return widget;
        });
    }

    protected void attachDefaultChangeListeners(ConfigValue<?> value, ThemeWidget widget, ThemedButtonWidget revertButton, ThemedButtonWidget revertToDefault) {
        widget.setChangeListener(_ -> {
            revertButton.setActive(value.isEditable() && value.isChanged());
            revertToDefault.setActive(value.isEditable() && value.isChangedFromDefault());
        });
    }

    @FunctionalInterface
    public interface ValueReverter {
        void revert(boolean toDefault);
    }
}
