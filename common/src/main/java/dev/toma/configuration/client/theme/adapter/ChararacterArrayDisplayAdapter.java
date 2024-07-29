package dev.toma.configuration.client.theme.adapter;

import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.ThemedButtonWidget;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.value.CharArrayValue;
import dev.toma.configuration.config.value.CharValue;
import dev.toma.configuration.config.value.ConfigValue;

import java.lang.reflect.Field;

public class ChararacterArrayDisplayAdapter extends AbstractArrayDisplayAdapter<Character> {

    @Override
    public void placeWidgets(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        CharArrayValue array = (CharArrayValue) value;
        ThemedButtonWidget buttonWidget = initButton(container, theme, holder, array, Character.class, field, CharValue::new);
        ValueReverter reverter = createReverter(array, buttonWidget);
        createControls(buttonWidget, array, theme, container, reverter);
    }
}
