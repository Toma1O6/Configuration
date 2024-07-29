package dev.toma.configuration.client.theme.adapter;

import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.ThemedButtonWidget;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.ShortArrayValue;
import dev.toma.configuration.config.value.ShortValue;

import java.lang.reflect.Field;

public class ShortArrayDisplayAdapter extends AbstractArrayDisplayAdapter<Short> {

    @Override
    public void placeWidgets(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        ShortArrayValue array = (ShortArrayValue) value;
        ThemedButtonWidget buttonWidget = initButton(container, theme, holder, array, Short.class, field, ShortValue::new);
        ValueReverter reverter = createReverter(array, buttonWidget);
        createControls(buttonWidget, array, theme, container, reverter);
    }
}
