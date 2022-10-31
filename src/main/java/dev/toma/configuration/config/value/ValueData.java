package dev.toma.configuration.config.value;

import dev.toma.configuration.config.adapter.TypeAdapter;

import javax.annotation.Nullable;

public final class ValueData<T> implements ICommentsProvider {

    private final String id;
    private final String[] tooltip;
    private final T defaultValue;
    private final TypeAdapter.AdapterContext context;
    @Nullable
    private ConfigValue<?> parent;

    private ValueData(String id, String[] tooltip, T defaultValue, TypeAdapter.AdapterContext context) {
        this.id = id;
        this.tooltip = tooltip;
        this.defaultValue = defaultValue;
        this.context = context;
    }

    public static <V> ValueData<V> of(String id, V value, TypeAdapter.AdapterContext setter, String... comments) {
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
        this.context.setFieldValue(value);
    }

    public void setParent(@Nullable ConfigValue<?> parent) {
        this.parent = parent;
    }

    @Nullable
    public ConfigValue<?> getParent() {
        return parent;
    }

    public TypeAdapter.AdapterContext getContext() {
        return context;
    }
}
