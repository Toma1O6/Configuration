package dev.toma.configuration.client.theme.adapter;

import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.screen.WidgetPlacerHelper;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.BooleanWidget;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.value.BooleanValue;
import dev.toma.configuration.config.value.ConfigValue;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;

public class BooleanDisplayAdapter extends AbstractDisplayAdapter {

    public static final Component TRUE = Component.translatable("text.configuration.value.true").withStyle(ChatFormatting.GREEN);
    public static final Component FALSE = Component.translatable("text.configuration.value.false").withStyle(ChatFormatting.RED);
    private Component enabledText;
    private Component disabledText;

    public BooleanDisplayAdapter() {
        this.enabledText = TRUE;
        this.disabledText = FALSE;
    }

    @Override
    public void placeWidgets(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        BooleanValue booleanValue = (BooleanValue) value;
        BooleanWidget widget = container.addConfigWidget((x, y, width, height, configId) -> {
            int left = WidgetPlacerHelper.getLeft(x, width);
            int widgetWidth = WidgetPlacerHelper.getWidth(width);
            return createWidget(left, y, widgetWidth, height, theme, booleanValue);
        });
        widget.setBackgroundRenderer(theme.getButtonBackground(widget));
        ValueReverter reverter = useDefault -> widget.setState(useDefault ? booleanValue.getValueData().getDefaultValue() : !booleanValue.get());
        this.createControls(widget, value, theme, container, reverter);
    }

    protected BooleanWidget createWidget(int x, int y, int width, int height, ConfigTheme theme, BooleanValue config) {
        return new BooleanWidget(x, y, width, height, theme, config, this.enabledText, this.disabledText);
    }

    public void setEnabledText(Component enabledText) {
        this.enabledText = enabledText;
    }

    public void setDisabledText(Component disabledText) {
        this.disabledText = disabledText;
    }
}
