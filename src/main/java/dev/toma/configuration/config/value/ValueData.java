package dev.toma.configuration.config.value;

import dev.toma.configuration.config.adapter.TypeAdapter;

public final class ValueData<T> implements ICommentsProvider {

    private final String id;
    private final String[] tooltip;
    private final T defaultValue;
    private final TypeAdapter.SetField setter;

    private ValueData(String id, String[] tooltip, T defaultValue, TypeAdapter.SetField setter) {
        this.id = id;
        this.tooltip = tooltip;
        this.defaultValue = defaultValue;
        this.setter = setter;
    }

    public static <V> ValueData<V> of(String id, V value, TypeAdapter.SetField setter, String... comments) {
        return new ValueData<>(id, comments, value, setter);
    }

    public String getId() {
        return id;
    }

    @Override
    public String[] getComments() {
        return tooltip;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public void setValueToMemory(Object value) {
        this.setter.setFieldValue(value);
    }
}
