package dev.toma.configuration.config.value;

import dev.toma.configuration.config.NumberDisplayType;

import javax.annotation.Nullable;
import java.text.DecimalFormat;

public interface NumericArrayValue extends ArrayValue {

    NumberDisplayType getDisplayType();

    @Nullable
    DecimalFormat getDecimalFormat();

    @Override
    default String elementToString(Object element) {
        DecimalFormat df = this.getDecimalFormat();
        return df != null ? df.format(element) : ArrayValue.super.elementToString(element);
    }
}
