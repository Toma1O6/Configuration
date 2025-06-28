package dev.toma.configuration.config.value;

import dev.toma.configuration.config.ConfigUtils;
import dev.toma.configuration.config.util.NumericRangeDescription;
import dev.toma.configuration.config.validate.NumberRange;

import java.lang.reflect.Field;

public abstract class NumericValue<T extends Number & Comparable<T>> extends ConfigValue<T> implements INumericValue<T> {

    private final T minValue;
    private final T maxValue;
    private NumberRange<T> range;

    public NumericValue(ValueData<T> data, T minValue, T maxValue) {
        super(data);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.addFixer(this::fixValue);
    }

    @Override
    public final T min() {
        return this.minValue;
    }

    @Override
    public final T max() {
        return this.maxValue;
    }

    @Override
    public final NumberRange<T> getRange() {
        return this.range;
    }

    public final double getSliderValue() {
        return this.getSliderValue(this.get());
    }

    public final double getSliderValue(T num) {
        double current = num.doubleValue();
        return (current - this.range.min().doubleValue()) / (this.range.max().doubleValue() - this.range.min().doubleValue());
    }

    public abstract T getValueFromSlider(double sliderValue);

    protected abstract NumberRange<T> getValueRange(Field field, T min, T max);

    @Override
    protected void processAdditionalAnnotations(Field field) {
        this.range = this.getValueRange(field, this.minValue, this.maxValue);
        this.addDescriptionProvider(NumericRangeDescription.create());
    }

    private T fixValue(T in) {
        if (this.range != null && !this.range.isWithinRange(in)) {
            T clamped = this.range.clamp(in);
            ConfigUtils.logCorrectedMessage(this.getId(), in, clamped);
            return clamped;
        }
        return in;
    }
}
