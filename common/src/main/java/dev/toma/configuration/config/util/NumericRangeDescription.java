package dev.toma.configuration.config.util;

import dev.toma.configuration.config.validate.NumberRange;
import dev.toma.configuration.config.value.IConfigValueReadable;
import dev.toma.configuration.config.value.INumericValue;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Consumer;

public class NumericRangeDescription<N extends Number & Comparable<N>> extends NoteDescriptionProvider<N> {

    public static final String LOCALIZATION_KEY = "text.configuration.description.range";

    private NumericRangeDescription() {}

    public static <N extends Number & Comparable<N>> IDescriptionProvider<N> create() {
        return new NumericRangeDescription<>();
    }

    @Override
    public void appendValues(IConfigValueReadable<N> value, Consumer<MutableComponent> appender) {
        if (!(value instanceof INumericValue<?> numericValue)) {
            return;
        }
        MutableComponent template = getTemplate(numericValue);
        if (template == null) {
            return;
        }
        appender.accept(template);
    }

    public static <N extends Number & Comparable<N>> MutableComponent getTemplate(INumericValue<N> value) {
        NumberRange<N> range = value.getRange();
        N min = value.min();
        N max = value.max();
        N rangeMin = range.min();
        N rangeMax = range.max();
        if (rangeMin.compareTo(min) <= 0 && rangeMax.compareTo(max) >= 0) {
            return null; // Full range available
        } else if (rangeMin.compareTo(min) > 0 && rangeMax.compareTo(max) < 0) {
            return Component.translatable(LOCALIZATION_KEY, rangeMin.doubleValue(), rangeMax.doubleValue());
        } else if (rangeMin.compareTo(min) > 0) {
            return Component.translatable(LOCALIZATION_KEY, rangeMin.doubleValue(), "...");
        } else {
            return Component.translatable(LOCALIZATION_KEY, "...", rangeMax.doubleValue());
        }
    }
}
