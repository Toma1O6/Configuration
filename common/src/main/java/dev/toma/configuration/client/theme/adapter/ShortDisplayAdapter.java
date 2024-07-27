package dev.toma.configuration.client.theme.adapter;

import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.EditBoxWidget;
import dev.toma.configuration.client.widget.SliderWidget;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.ShortValue;

import java.lang.reflect.Field;

public class ShortDisplayAdapter extends AbstractNumericDisplayAdapter {

    @Override
    protected void placeTextField(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        ShortValue shortValue = (ShortValue) value;
        EditBoxWidget editBox = initEditBox(container, theme, shortValue, field);
        editBox.setFilter(AbstractNumericDisplayAdapter::allowIntegerCharacters);
        handleValueChanged(editBox, shortValue, Short::parseShort, container);
        placeEditBoxControls(shortValue, theme, editBox, container);
    }

    @Override
    protected void placeSlider(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        ShortValue shortValue = (ShortValue) value;
        SliderWidget<Short> slider = initSlider(container, theme, shortValue, field);
        placeSliderControls(shortValue, theme, slider, container);
    }
}
