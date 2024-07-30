package dev.toma.configuration.client.theme.adapter;

import dev.toma.configuration.client.ClientErrors;
import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.screen.WidgetPlacerHelper;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.EditBoxWidget;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.validate.IValidationResult;
import dev.toma.configuration.config.value.CharValue;
import dev.toma.configuration.config.value.ConfigValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

import java.lang.reflect.Field;

public class CharacterDisplayAdapter extends AbstractDisplayAdapter {

    @Override
    public void placeWidgets(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        CharValue charValue = (CharValue) value;
        Font font = Minecraft.getInstance().font;
        EditBoxWidget editBoxWidget = container.addConfigWidget((x, y, width, height, configId) -> {
            int left = WidgetPlacerHelper.getLeft(x, width);
            int widgetWidth = WidgetPlacerHelper.getWidth(width);
            return new EditBoxWidget(left, y, widgetWidth, height, theme, font);
        });
        editBoxWidget.setValue(charValue.get().toString());
        editBoxWidget.setMaxLength(1);
        editBoxWidget.setResponder(text -> {
            if (!text.isEmpty()) {
                container.setOkStatus();
                char val = text.charAt(0);
                charValue.setValue(val);
            } else {
                container.setValidationResult(IValidationResult.error(ClientErrors.CHAR_VALUE_EMPTY));
            }
            editBoxWidget.setChanged();
        });
        editBoxWidget.setBackgroundRenderer(theme.getEditBoxBackground(editBoxWidget));

        ValueReverter valueReverter = useDefault -> editBoxWidget.setValue(String.valueOf(useDefault ? charValue.getValueData().getDefaultValue() : charValue.getActiveValue()));
        createControls(editBoxWidget, charValue, theme, container, valueReverter);
    }
}
