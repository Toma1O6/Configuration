package dev.toma.configuration.client.theme.adapter;

import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.ThemedButtonWidget;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.IntArrayValue;
import dev.toma.configuration.config.value.IntValue;

import java.lang.reflect.Field;

public class IntegerArrayDisplayAdapter extends AbstractArrayDisplayAdapter<Integer> {

    @Override
    public void placeWidgets(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        IntArrayValue array = (IntArrayValue) value;
        ThemedButtonWidget buttonWidget = initButton(container, theme, holder, array, Integer.class, field, IntValue::new);
        ValueReverter reverter = createReverter(array, buttonWidget);
        createControls(buttonWidget, array, theme, container, reverter);
    }
}
