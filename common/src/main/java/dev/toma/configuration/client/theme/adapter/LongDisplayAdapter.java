package dev.toma.configuration.client.theme.adapter;

import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.EditBoxWidget;
import dev.toma.configuration.client.widget.SliderWidget;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.LongValue;

import java.lang.reflect.Field;

public class LongDisplayAdapter extends AbstractNumericDisplayAdapter {

    @Override
    protected void placeTextField(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        LongValue longValue = (LongValue) value;
        EditBoxWidget editBox = initEditBox(container, theme, longValue, field);
        editBox.setFilter(AbstractNumericDisplayAdapter::allowIntegerCharacters);
        handleValueChanged(editBox, longValue, Long::parseLong, container);
        placeEditBoxControls(longValue, theme, editBox, container);
    }

    @Override
    protected void placeSlider(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        LongValue longValue = (LongValue) value;
        SliderWidget<Long> slider = initSlider(container, theme, longValue, field);
        placeSliderControls(longValue, theme, slider, container);
    }
}
