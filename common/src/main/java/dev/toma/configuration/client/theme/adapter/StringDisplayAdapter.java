package dev.toma.configuration.client.theme.adapter;

import dev.toma.configuration.client.ClientErrors;
import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.screen.WidgetPlacerHelper;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.ColorWidget;
import dev.toma.configuration.client.widget.EditBoxWidget;
import dev.toma.configuration.client.widget.ThemedButtonWidget;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.ConfigUtils;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.validate.IValidationResult;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.StringValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringDisplayAdapter extends AbstractDisplayAdapter {

    @Override
    public void placeWidgets(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        Configurable.Gui.ColorValue colorValue = field.getAnnotation(Configurable.Gui.ColorValue.class);
        StringValue stringValue = (StringValue) value;

        Font font = Minecraft.getInstance().font;
        EditBoxWidget editBox = container.addConfigWidget((x, y, width, height, configId) -> {
            int left = WidgetPlacerHelper.getLeft(x, width);
            int widgetWidth = WidgetPlacerHelper.getWidth(width);
            if (colorValue != null) {
                left += 21;
                widgetWidth -= 21;
            }
            return new EditBoxWidget(left, y, widgetWidth, height, theme, font);
        });
        editBox.setValue(stringValue.get());
        editBox.setResponder(text -> this.onTextChanged(text, stringValue, container, editBox));
        editBox.setBackgroundRenderer(theme.getEditBoxBackground(editBox));
        ConfigUtils.adjustCharacterLimit(field, editBox);

        ValueReverter reverter = useDefault -> editBox.setValue(useDefault ? stringValue.getValueData().getDefaultValue() : stringValue.getActiveValue());
        ThemedButtonWidget revert = this.createRevertButton(editBox, value, theme, container, reverter);
        ThemedButtonWidget revertDefault = this.createRevertToDefaultButton(editBox, value, theme, container, reverter);
        attachDefaultChangeListeners(stringValue, editBox, revert, revertDefault);

        if (colorValue != null) {
            ColorWidget colorWidget = createColorSelectorWidget(colorValue, container, editBox);
            attachDefaultChangeListeners(stringValue, colorWidget, revert, revertDefault);
        }
    }

    protected ColorWidget createColorSelectorWidget(Configurable.Gui.ColorValue colorValue, WidgetAdder container, EditBoxWidget editBox) {
        ConfigTheme theme = editBox.getTheme();
        ColorWidget widget = container.addConfigWidget((x, y, width, height, configId) -> {
            int left = WidgetPlacerHelper.getLeft(x, width);
            int widgetWidth = 20;
            ColorWidget.GetSet<String> getSet = ColorWidget.GetSet.of(editBox::getValue, editBox::setValue);
            Screen currentScreen = Minecraft.getInstance().screen;
            return new ColorWidget(left, y, widgetWidth, height, theme, colorValue, getSet, currentScreen);
        });
        widget.setBackgroundRenderer(theme.getColorBackground(widget));
        return widget;
    }

    private void onTextChanged(String text, StringValue value, WidgetAdder container, EditBoxWidget editBox) {
        Pattern pattern = value.getPattern();
        if (pattern != null) {
            Matcher matcher = pattern.matcher(text);
            if (!matcher.matches()) {
                String errorMessage = value.getErrorDescriptor();
                MutableComponent errorLabel = errorMessage != null ? Component.translatable(errorMessage, text, pattern) : ClientErrors.invalidText(text, pattern);
                container.setValidationResult(IValidationResult.error(errorLabel));
                return;
            }
        }
        container.setOkStatus();
        value.setValue(text);
        editBox.setChanged();
    }
}
