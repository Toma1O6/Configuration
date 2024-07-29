package dev.toma.configuration.client.theme.adapter;

import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.ThemedButtonWidget;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.StringArrayValue;
import dev.toma.configuration.config.value.StringValue;

import java.lang.reflect.Field;

public class StringArrayDisplayAdapter extends AbstractArrayDisplayAdapter<String> {

    @Override
    public void placeWidgets(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        StringArrayValue array = (StringArrayValue) value;
        ThemedButtonWidget buttonWidget = initButton(container, theme, holder, array, String.class, field, StringValue::new);
        ValueReverter reverter = createReverter(array, buttonWidget);
        createControls(buttonWidget, array, theme, container, reverter);
    }
}
