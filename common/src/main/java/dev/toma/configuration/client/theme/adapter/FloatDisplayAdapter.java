package dev.toma.configuration.client.theme.adapter;

import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.EditBoxWidget;
import dev.toma.configuration.client.widget.SliderWidget;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.FloatValue;

import java.lang.reflect.Field;

public class FloatDisplayAdapter extends AbstractNumericDisplayAdapter {

    @Override
    protected void placeTextField(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        FloatValue floatValue = (FloatValue) value;
        EditBoxWidget editBox = initEditBox(container, theme, floatValue, field);
        editBox.setFilter(AbstractNumericDisplayAdapter::allowDecimalCharacters);
        handleValueChanged(editBox, floatValue, Float::parseFloat, container);
        placeEditBoxControls(floatValue, theme, editBox, container);
    }

    @Override
    protected void placeSlider(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        FloatValue floatValue = (FloatValue) value;
        SliderWidget<Float> slider = initSlider(container, theme, floatValue, field);
        placeSliderControls(floatValue, theme, slider, container);
    }
}
