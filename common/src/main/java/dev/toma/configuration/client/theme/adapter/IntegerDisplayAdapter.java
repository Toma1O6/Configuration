package dev.toma.configuration.client.theme.adapter;

import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.EditBoxWidget;
import dev.toma.configuration.client.widget.SliderWidget;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.IntValue;

import java.lang.reflect.Field;

public class IntegerDisplayAdapter extends AbstractNumericDisplayAdapter {

    @Override
    protected void placeTextField(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        IntValue intValue = (IntValue) value;
        EditBoxWidget editBox = initEditBox(container, theme, intValue, field);
        editBox.setFilter(AbstractNumericDisplayAdapter::allowIntegerCharacters);
        handleValueChanged(editBox, intValue, Integer::parseInt, container);
        placeEditBoxControls(intValue, theme, editBox, container);
    }

    @Override
    protected void placeSlider(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        IntValue intValue = (IntValue) value;
        SliderWidget<Integer> slider = initSlider(container, theme, intValue, field);
        placeSliderControls(intValue, theme, slider, container);
    }
}
