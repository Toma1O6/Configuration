package dev.toma.configuration.client.theme.adapter;

import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.ThemedButtonWidget;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.value.BooleanArrayValue;
import dev.toma.configuration.config.value.BooleanValue;
import dev.toma.configuration.config.value.ConfigValue;

import java.lang.reflect.Field;

public class BooleanArrayDisplayAdapter extends AbstractArrayDisplayAdapter<Boolean> {

    @Override
    public void placeWidgets(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        BooleanArrayValue array = (BooleanArrayValue) value;
        ThemedButtonWidget buttonWidget = initButton(container, theme, holder, array, Boolean.class, field, BooleanValue::new);
        ValueReverter reverter = createReverter(array, buttonWidget);
        createControls(buttonWidget, array, theme, container, reverter);
    }
}
