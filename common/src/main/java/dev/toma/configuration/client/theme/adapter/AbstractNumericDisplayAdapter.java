package dev.toma.configuration.client.theme.adapter;

import dev.toma.configuration.client.ClientErrors;
import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.screen.WidgetPlacerHelper;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.EditBoxWidget;
import dev.toma.configuration.client.widget.SliderWidget;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.ConfigUtils;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.validate.IValidationResult;
import dev.toma.configuration.config.validate.NumberRange;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.NumericValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractNumericDisplayAdapter extends AbstractDisplayAdapter {

    public static final Pattern INTEGER_CHARS_PATTERN = Pattern.compile("^[-0-9]+$");
    public static final Pattern DECIMAL_CHARS_PATTERN = Pattern.compile("^[-0-9.Ee]+$");

    @Override
    public final void placeWidgets(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container) {
        boolean isSlider = field.isAnnotationPresent(Configurable.Gui.Slider.class);
        if (isSlider) {
            placeSlider(holder, value, field, theme, container);
        } else {
            placeTextField(holder, value, field, theme, container);
        }
    }

    protected abstract void placeTextField(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container);

    protected abstract void placeSlider(ConfigHolder<?> holder, ConfigValue<?> value, Field field, ConfigTheme theme, WidgetAdder container);

    protected <T extends Number & Comparable<T>> void placeEditBoxControls(NumericValue<T> value, ConfigTheme theme, EditBoxWidget widget, WidgetAdder container) {
        ValueReverter reverter = useDefault -> widget.setValue(String.valueOf(useDefault ? value.getValueData().getDefaultValue() : value.getActiveValue()));
        createControls(widget, value, theme, container, reverter);
    }

    protected <T extends Number & Comparable<T>> void placeSliderControls(NumericValue<T> value, ConfigTheme theme, SliderWidget<T> widget, WidgetAdder container) {
        ValueReverter reverter = useDefault -> widget.setValue(value.getSliderValue(useDefault ? value.getValueData().getDefaultValue() : value.getActiveValue()));
        createControls(widget, value, theme, container, reverter);
    }

    public <T extends Number & Comparable<T>> EditBoxWidget initEditBox(WidgetAdder container, ConfigTheme theme, NumericValue<T> value, Field field) {
        Font font = Minecraft.getInstance().font;
        EditBoxWidget editBoxWidget = container.addConfigWidget((x, y, width, height, configId) -> {
            int left = WidgetPlacerHelper.getLeft(x, width);
            int widgetWidth = WidgetPlacerHelper.getWidth(width);
            return new EditBoxWidget(left, y, widgetWidth, height, theme, font);
        });
        editBoxWidget.setValue(value.get().toString());
        editBoxWidget.setBackgroundRenderer(theme.getEditBoxBackground(editBoxWidget));
        ConfigUtils.adjustCharacterLimit(field, editBoxWidget);
        DecimalFormat decimalFormat = ConfigUtils.getDecimalFormat(field);
        editBoxWidget.setFormatter(decimalFormat, value::get);
        return editBoxWidget;
    }

    public <T extends Number & Comparable<T>> SliderWidget<T> initSlider(WidgetAdder container, ConfigTheme theme, NumericValue<T> value, Field field) {
        Font font = Minecraft.getInstance().font;
        SliderWidget<T> slider = container.addConfigWidget((x, y, width, height, configId) -> {
            int left = WidgetPlacerHelper.getLeft(x, width);
            int widgetWidth = WidgetPlacerHelper.getWidth(width);
            return new SliderWidget<>(left, y, widgetWidth, height, theme, value, font);
        });
        slider.setBackgroundRenderer(theme.getSliderBackground(slider));
        slider.setHandleRenderer(theme.getSliderHandle(slider));
        DecimalFormat decimalFormat = ConfigUtils.getDecimalFormat(field);
        slider.setFormatter(decimalFormat);
        return slider;
    }

    public <T extends Number & Comparable<T>> void handleValueChanged(EditBoxWidget editBox, NumericValue<T> value, Function<String, T> parser, WidgetAdder container) {
        editBox.setResponder(text -> {
            T parsed;
            try {
                parsed = parser.apply(text);
            } catch (NumberFormatException e) {
                container.setValidationResult(IValidationResult.error(ClientErrors.notANumber(text)));
                return;
            }
            NumberRange<T> range = value.getRange();
            if (!range.isWithinRange(parsed)) {
                container.setValidationResult(IValidationResult.error(ClientErrors.outOfBounds(parsed, range)));
                return;
            }
            container.setOkStatus();
            value.setValue(parsed);
            editBox.setChanged();
        });
    }

    public static boolean allowIntegerCharacters(String text) {
        Matcher matcher = INTEGER_CHARS_PATTERN.matcher(text);
        return text.isEmpty() || matcher.matches();
    }

    public static boolean allowDecimalCharacters(String text) {
        Matcher matcher = DECIMAL_CHARS_PATTERN.matcher(text);
        return text.isEmpty() || matcher.matches();
    }
}
