package dev.toma.configuration.client.theme.adapter;

import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.screen.AbstractConfigScreen;
import dev.toma.configuration.client.widget.AbstractThemeWidget;
import dev.toma.configuration.client.widget.ThemedButtonWidget;
import dev.toma.configuration.client.widget.render.SpriteBackgroundRenderer;
import dev.toma.configuration.config.value.ConfigValue;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;

import java.time.Duration;

public abstract class AbstractAdapter implements DisplayAdapter {

    protected ThemedButtonWidget createRevertButton(AbstractThemeWidget linkedTo, ConfigValue<?> value, WidgetAdder adder, ValueReverter reverter) {
        return adder.addConfigWidget(false, (x, y, width, height, id) -> {
            int left = linkedTo.getRight() + 1;
            ThemedButtonWidget widget = new ThemedButtonWidget(left, linkedTo.getY(), 20, linkedTo.getHeight(), CommonComponents.EMPTY, linkedTo.getTheme());
            widget.setClickListener((widget1, mouseX, mouseY) -> reverter.revert(false));
            widget.setTooltip(Tooltip.create(AbstractThemeWidget.REVERT));
            widget.setTooltipDelay(Duration.ofMillis(200));
            widget.setBackgroundRenderer(new SpriteBackgroundRenderer(() -> AbstractConfigScreen.BUTTON_SPRITES.get(widget.active, widget.isHoveredOrFocused())));
            widget.active = value.isEditable() && value.isChanged();
            return widget;
        });
    }

    protected ThemedButtonWidget createRevertToDefaultButton(AbstractThemeWidget linkedTo, ConfigValue<?> value, WidgetAdder adder, ValueReverter reverter) {
        return adder.addConfigWidget(false, (x, y, width, height, id) -> {
            int left = linkedTo.getRight() + 22;
            ThemedButtonWidget widget = new ThemedButtonWidget(left, linkedTo.getY(), 20, linkedTo.getHeight(), CommonComponents.EMPTY, linkedTo.getTheme());
            widget.setClickListener((widget1, mouseX, mouseY) -> reverter.revert(true));
            widget.setTooltip(Tooltip.create(AbstractThemeWidget.REVERT_DEFAULT));
            widget.setTooltipDelay(Duration.ofMillis(200));
            widget.setBackgroundRenderer(new SpriteBackgroundRenderer(() -> AbstractConfigScreen.BUTTON_SPRITES.get(widget.active, widget.isHoveredOrFocused())));
            widget.active = value.isEditable() && value.isChangedFromDefault();
            return widget;
        });
    }

    protected void attachDefaultChangeListeners(ConfigValue<?> value, AbstractThemeWidget widget, ThemedButtonWidget revertButton, ThemedButtonWidget revertToDefault) {
        widget.setChangeListener(w -> {
            revertButton.active = value.isEditable() && value.isChanged();
            revertToDefault.active = value.isEditable() && value.isChangedFromDefault();
        });
    }

    public interface ValueReverter {
        void revert(boolean toDefault);
    }
}
