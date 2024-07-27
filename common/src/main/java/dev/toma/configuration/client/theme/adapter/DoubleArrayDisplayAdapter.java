package dev.toma.configuration.client.theme.adapter;

import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.ThemedButtonWidget;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.DoubleArrayValue;
import dev.toma.configuration.config.value.DoubleValue;

import java.lang.reflect.Field;

public class DoubleArrayDisplayAdapter extends AbstractArrayDisplayAdapter<Double> {

    @Override
    public void placeWidgets(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        DoubleArrayValue array = (DoubleArrayValue) value;
        ThemedButtonWidget buttonWidget = initButton(container, theme, holder, array, Double.class, field, DoubleValue::new);
        ValueReverter reverter = createReverter(array, buttonWidget);
        createControls(buttonWidget, array, theme, container, reverter);
    }
}
