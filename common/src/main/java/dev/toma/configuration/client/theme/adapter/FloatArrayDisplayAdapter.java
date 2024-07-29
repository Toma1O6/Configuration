package dev.toma.configuration.client.theme.adapter;

import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.ThemedButtonWidget;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.FloatArrayValue;
import dev.toma.configuration.config.value.FloatValue;

import java.lang.reflect.Field;

public class FloatArrayDisplayAdapter extends AbstractArrayDisplayAdapter<Float> {

    @Override
    public void placeWidgets(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        FloatArrayValue array = (FloatArrayValue) value;
        ThemedButtonWidget buttonWidget = initButton(container, theme, holder, array, Float.class, field, FloatValue::new);
        ValueReverter reverter = createReverter(array, buttonWidget);
        createControls(buttonWidget, array, theme, container, reverter);
    }
}
