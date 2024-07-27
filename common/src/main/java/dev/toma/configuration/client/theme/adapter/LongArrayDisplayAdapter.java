package dev.toma.configuration.client.theme.adapter;

import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.ThemedButtonWidget;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.LongArrayValue;
import dev.toma.configuration.config.value.LongValue;

import java.lang.reflect.Field;

public class LongArrayDisplayAdapter extends AbstractArrayDisplayAdapter<Long> {

    @Override
    public void placeWidgets(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        LongArrayValue array = (LongArrayValue) value;
        ThemedButtonWidget buttonWidget = initButton(container, theme, holder, array, Long.class, field, LongValue::new);
        ValueReverter reverter = createReverter(array, buttonWidget);
        createControls(buttonWidget, array, theme, container, reverter);
    }
}
