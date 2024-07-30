package dev.toma.configuration.client.theme.adapter;

import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.screen.WidgetPlacerHelper;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.EnumWidget;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.EnumValue;

import java.lang.reflect.Field;

public class EnumDisplayAdapter<E extends Enum<E>> extends AbstractDisplayAdapter {

    @SuppressWarnings("unchecked")
    @Override
    public void placeWidgets(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        EnumValue<E> enumValue = (EnumValue<E>) value;
        EnumWidget<E> widget = container.addConfigWidget((x, y, width, height, configId) -> {
            int left = WidgetPlacerHelper.getLeft(x, width);
            int widgetWidth = WidgetPlacerHelper.getWidth(width);
            return new EnumWidget<>(left, y, widgetWidth, height, theme, enumValue);
        });
        widget.setBackgroundRenderer(theme.getButtonBackground(widget));
        ValueReverter reverter = def -> widget.setValue(def ? enumValue.getValueData().getDefaultValue() : enumValue.getActiveValue());
        createControls(widget, enumValue, theme, container, reverter);
    }
}
