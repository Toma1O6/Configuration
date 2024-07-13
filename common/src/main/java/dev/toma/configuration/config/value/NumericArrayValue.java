package dev.toma.configuration.config.value;

import dev.toma.configuration.config.ConfigUtils;

import java.lang.reflect.Field;

public abstract class NumericArrayValue<T extends Number & Comparable<T>> extends AbstractArrayValue<T> {

    private final T minValue;
    private final T maxValue;
    private ValueRange<T> range;

    public NumericArrayValue(ValueData<T[]> valueData, T minValue, T maxValue) {
        super(valueData);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public abstract ValueRange<T> getValueRange(Field field, T typeMin, T typeMax);

    @Override
    protected void readFieldData(Field field) {
        super.readFieldData(field);
        this.range = this.getValueRange(field, this.minValue, this.maxValue);
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

    public ValueRange<T> getRange() {
        return range;
    }

    public record ValueRange<T extends Number & Comparable<T>>(T min, T max) {

        public boolean isWithinRange(T t) {
            int minBoundCompare = t.compareTo(this.min());
            if (minBoundCompare < 0) {
                return false;
            }
            int maxBoundCompare = t.compareTo(this.max());
            return maxBoundCompare <= 0;
        }

        public T clamp(T t) {
            int minBound = t.compareTo(this.min());
            if (minBound < 0) {
                return min();
            }
            int maxBound = t.compareTo(this.max());
            return maxBound > 0 ? max() : t;
        }
    }
}
