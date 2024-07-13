package dev.toma.configuration.config.value;

import dev.toma.configuration.config.ConfigUtils;
import dev.toma.configuration.config.validate.NumberRange;

import java.lang.reflect.Field;

public abstract class NumericArrayValue<T extends Number & Comparable<T>> extends AbstractArrayValue<T> implements INumericValue<T> {

    private final T minValue;
    private final T maxValue;
    private NumberRange<T> range;

    public NumericArrayValue(ValueData<T[]> valueData, T minValue, T maxValue) {
        super(valueData);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public abstract NumberRange<T> getValueRange(Field field);

    @Override
    protected void readFieldData(Field field) {
        super.readFieldData(field);
        this.range = this.getValueRange(field);
    }

    @Override
    protected T[] validateValue(T[] in) {
        T[] updatedArray = super.validateValue(in);
        for (int i = 0; i < updatedArray.length; i++) {
            T num = updatedArray[i];
            if (this.range != null && !this.range.isWithinRange(num)) {
                T clamped = this.range.clamp(num);
                ConfigUtils.logCorrectedMessage(this.getId() + "[" + i + "]", num, clamped);
                updatedArray[i] = clamped;
            }
        }
        return updatedArray;
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
}
