package dev.toma.configuration.client.theme.adapter;

import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.ThemedButtonWidget;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.EnumArrayValue;
import dev.toma.configuration.config.value.EnumValue;

import java.lang.reflect.Field;

public class EnumArrayDisplayAdapter<E extends Enum<E>> extends AbstractArrayDisplayAdapter<E> {

    @SuppressWarnings("unchecked")
    @Override
    public void placeWidgets(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        EnumArrayValue<E> array = (EnumArrayValue<E>) value;
        ThemedButtonWidget buttonWidget = initButton(container, theme, holder, array, array.getElementType(), field, EnumValue::new);
        ValueReverter reverter = createReverter(array, buttonWidget);
        createControls(buttonWidget, array, theme, container, reverter);
    }
}
