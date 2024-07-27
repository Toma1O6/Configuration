package dev.toma.configuration.client.theme.adapter;

import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.screen.WidgetPlacerHelper;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.AbstractThemeWidget;
import dev.toma.configuration.client.widget.EditBoxWidget;
import dev.toma.configuration.client.widget.SliderWidget;
import dev.toma.configuration.client.widget.ThemedButtonWidget;
import dev.toma.configuration.client.widget.render.IBackgroundRenderer;
import dev.toma.configuration.client.widget.render.SpriteBackgroundRenderer;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.ConfigUtils;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.IConfigValue;
import dev.toma.configuration.config.value.IntValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

import java.lang.reflect.Field;

public class IntegerDisplayAdapter extends AbstractNumericAdapter {

    @Override
    protected void placeTextField(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        IntValue intValue = (IntValue) value;
        Font font = Minecraft.getInstance().font;
        EditBoxWidget editBox = container.addConfigWidget((x, y, width, height, configId) -> {
            int left = WidgetPlacerHelper.getLeft(x, width);
            int widgetWidth = WidgetPlacerHelper.getWidth(width);
            return new EditBoxWidget(left, y, widgetWidth, height, theme, font);
        });
        editBox.setValue(String.valueOf(intValue.get()));
        editBox.setFilter(s -> ConfigUtils.containsOnlyValidCharacters(s, ConfigUtils.INTEGER_CHARS));
        handleValueChanged(editBox, intValue, Integer::parseInt, container);
        ConfigUtils.adjustCharacterLimit(field, editBox);
        IBackgroundRenderer renderer = new SpriteBackgroundRenderer(() -> EditBoxWidget.SPRITES.get(editBox.isActive(), editBox.isHoveredOrFocused()));
        editBox.setBackgroundRenderer(renderer);
        ValueReverter reverter = useDefault -> editBox.setValue(String.valueOf(useDefault ? intValue.getValueData().getDefaultValue() : intValue.get(IConfigValue.Mode.SAVED)));
        this.createControls(editBox, intValue, container, reverter);
    }

    @Override
    protected void placeSlider(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        IntValue intValue = (IntValue) value;
        Font font = Minecraft.getInstance().font;
        SliderWidget<Integer> slider = container.addConfigWidget((x, y, width, height, configId) -> {
            int left = WidgetPlacerHelper.getLeft(x, width);
            int widgetWidth = WidgetPlacerHelper.getWidth(width);
            return new SliderWidget<>(left, y, widgetWidth, height, theme, intValue, font);
        });
        ValueReverter reverter = useDefault -> slider.setValue(intValue.getSliderValue(useDefault ? intValue.getValueData().getDefaultValue() : intValue.get(IConfigValue.Mode.SAVED)));
        this.createControls(slider, intValue, container, reverter);
    }

    private void createControls(AbstractThemeWidget widget, IntValue intValue, WidgetAdder container, ValueReverter reverter) {
        ThemedButtonWidget revertButton = this.createRevertButton(widget, intValue, container, reverter);
        ThemedButtonWidget revertDefaultButton = this.createRevertToDefaultButton(widget, intValue, container, reverter);
        attachDefaultChangeListeners(intValue, widget, revertButton, revertDefaultButton);
    }
}
