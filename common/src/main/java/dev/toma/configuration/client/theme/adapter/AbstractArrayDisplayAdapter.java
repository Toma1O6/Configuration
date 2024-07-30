package dev.toma.configuration.client.theme.adapter;

import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.screen.ArrayConfigScreen;
import dev.toma.configuration.client.screen.WidgetPlacerHelper;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.AbstractThemeWidget;
import dev.toma.configuration.client.widget.ConfigEntryWidget;
import dev.toma.configuration.client.widget.ThemedButtonWidget;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.value.AbstractArrayValue;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.ValueData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class AbstractArrayDisplayAdapter<T> extends AbstractDisplayAdapter {

    protected <C extends AbstractArrayValue<T>> ValueReverter createReverter(C array, AbstractThemeWidget widget) {
        return def -> {
            array.setValue(def ? array.getValueData().getDefaultValue() : array.getActiveValue());
            widget.setChanged();
        };
    }

    @SuppressWarnings("unchecked")
    protected <C extends AbstractArrayValue<T>> ThemedButtonWidget initButton(WidgetAdder container, ConfigTheme theme, ConfigHolder<?> holder, C array, Class<T> type, Field field, Function<ValueData<T>, ConfigValue<T>> factory) {
        Minecraft client = Minecraft.getInstance();
        BiConsumer<T, Integer> setter = (t, i) -> {
            T[] original = array.get();
            T[] arr = (T[]) Array.newInstance(type, original.length);
            System.arraycopy(original, 0, arr, 0, original.length);
            arr[i] = t;
            array.setValue(arr);
        };
        ThemedButtonWidget buttonWidget = container.addConfigWidget((x, y, width, height, configId) -> {
            int left = WidgetPlacerHelper.getLeft(x, width);
            int widgetWidth = WidgetPlacerHelper.getWidth(width);
            return new ThemedButtonWidget(left, y, widgetWidth, height, ConfigEntryWidget.OPEN, theme);
        });
        buttonWidget.setClickListener((widget, mouseX, mouseY) -> {
            Screen activeScreen = client.screen;
            ArrayConfigScreen<T, C> arrayConfigScreen = new ArrayConfigScreen<>(holder, array, activeScreen);
            arrayConfigScreen.fetchSize(() -> array.get().length);
            arrayConfigScreen.valueFactory((id, elementIndex) -> {
                T[] arr = array.get();
                T[] defaultArray = array.getValueData().getDefaultValue();
                T defaultValue = elementIndex < defaultArray.length ? defaultArray[elementIndex] : arr[elementIndex];
                TypeAdapter.TypeAttributes<?> parentAttributes = array.getValueData().getAttributes();
                TypeAdapter.TypeAttributes<T> typeAttributes = parentAttributes.child(id, defaultValue, ArrayConfigScreen.callbackCtx(field, type, setter, elementIndex));
                ConfigValue<T> value = factory.apply(ValueData.of(typeAttributes));
                value.forceSetValue(arr[elementIndex]);
                return value;
            });
            arrayConfigScreen.addElement(() -> {
                T[] arr = array.get();
                T[] expanded = (T[]) Array.newInstance(type, arr.length + 1);
                System.arraycopy(arr, 0, expanded, 0, arr.length);
                expanded[arr.length] = array.createElementInstance();
                array.setValue(expanded);
            });
            arrayConfigScreen.removeElement((i, trimmer) -> {
                T[] arr = array.get();
                T[] trimmed = (T[]) Array.newInstance(type, arr.length - 1);
                array.setValue(trimmer.trim(i, arr, trimmed));
            });
            client.setScreen(arrayConfigScreen);
        });
        buttonWidget.setBackgroundRenderer(theme.getButtonBackground(buttonWidget));
        return buttonWidget;
    }
}
