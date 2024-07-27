package dev.toma.configuration.client.theme.adapter;

import dev.toma.configuration.client.ClientErrors;
import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.EditBoxWidget;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.validate.NumberRange;
import dev.toma.configuration.config.validate.ValidationResult;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.NumericValue;

import java.lang.reflect.Field;
import java.util.function.Function;

public abstract class AbstractNumericAdapter extends AbstractAdapter {

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

    public static <T extends Number & Comparable<T>> void handleValueChanged(EditBoxWidget editBox, NumericValue<T> value, Function<String, T> parser, WidgetAdder container) {
        editBox.setResponder(text -> {
            T parsed;
            try {
                parsed = parser.apply(text);
            } catch (NumberFormatException e) {
                container.setValidationResult(ValidationResult.error(ClientErrors.notANumber(text)));
                return;
            }
            NumberRange<T> range = value.getRange();
            if (!range.isWithinRange(parsed)) {
                container.setValidationResult(ValidationResult.error(ClientErrors.outOfBounds(parsed, range)));
                return;
            }
            container.setOkStatus();
            value.setValue(parsed);
            editBox.setChanged();
        });
    }
}
