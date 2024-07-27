package dev.toma.configuration.client.theme.adapter;

import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.EditBoxWidget;
import dev.toma.configuration.client.widget.SliderWidget;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.DoubleValue;

import java.lang.reflect.Field;

public class DoubleDisplayAdapter extends AbstractNumericDisplayAdapter {

    @Override
    protected void placeTextField(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        DoubleValue doubleValue = (DoubleValue) value;
        EditBoxWidget editBox = initEditBox(container, theme, doubleValue, field);
        editBox.setFilter(AbstractNumericDisplayAdapter::allowDecimalCharacters);
        handleValueChanged(editBox, doubleValue, Double::parseDouble, container);
        placeEditBoxControls(doubleValue, theme, editBox, container);
    }

    @Override
    protected void placeSlider(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        DoubleValue doubleValue = (DoubleValue) value;
        SliderWidget<Double> slider = initSlider(container, theme, doubleValue, field);
        placeSliderControls(doubleValue, theme, slider, container);
    }
}
