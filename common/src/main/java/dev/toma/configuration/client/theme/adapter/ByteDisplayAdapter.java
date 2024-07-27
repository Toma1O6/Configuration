package dev.toma.configuration.client.theme.adapter;

import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.EditBoxWidget;
import dev.toma.configuration.client.widget.SliderWidget;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.value.ByteValue;
import dev.toma.configuration.config.value.ConfigValue;

import java.lang.reflect.Field;

public class ByteDisplayAdapter extends AbstractNumericDisplayAdapter {

    @Override
    protected void placeTextField(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        ByteValue byteValue = (ByteValue) value;
        EditBoxWidget editBox = initEditBox(container, theme, byteValue, field);
        editBox.setFilter(AbstractNumericDisplayAdapter::allowIntegerCharacters);
        handleValueChanged(editBox, byteValue, Byte::parseByte, container);
        placeEditBoxControls(byteValue, theme, editBox, container);
    }

    @Override
    protected void placeSlider(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        ByteValue byteValue = (ByteValue) value;
        SliderWidget<Byte> slider = initSlider(container, theme, byteValue, field);
        placeSliderControls(byteValue, theme, slider, container);
    }
}
