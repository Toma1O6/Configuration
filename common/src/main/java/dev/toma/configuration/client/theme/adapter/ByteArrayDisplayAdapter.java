package dev.toma.configuration.client.theme.adapter;

import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.ThemedButtonWidget;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.value.ByteArrayValue;
import dev.toma.configuration.config.value.ByteValue;
import dev.toma.configuration.config.value.ConfigValue;

import java.lang.reflect.Field;

public class ByteArrayDisplayAdapter extends AbstractArrayDisplayAdapter<Byte> {

    @Override
    public void placeWidgets(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        ByteArrayValue array = (ByteArrayValue) value;
        ThemedButtonWidget buttonWidget = initButton(container, theme, holder, array, Byte.class, field, ByteValue::new);
        ValueReverter reverter = createReverter(array, buttonWidget);
        createControls(buttonWidget, array, theme, container, reverter);
    }
}
