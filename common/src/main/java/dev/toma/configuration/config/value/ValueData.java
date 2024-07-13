package dev.toma.configuration.config.value;

import dev.toma.configuration.config.adapter.TypeAdapter;

public final class ValueData<T> implements IDescriptionProvider {

    private final TypeAdapter.TypeAttributes<T> attributes;
    private final Class<T> valueType;
    private ConfigValue<?> parent;

    @SuppressWarnings("unchecked")
    private ValueData(TypeAdapter.TypeAttributes<T> attributes) {
        this.attributes = attributes;
        this.valueType = (Class<T>) attributes.value().getClass();
    }

    public static <V> ValueData<V> of(TypeAdapter.TypeAttributes<V> attributes) {
        return new ValueData<>(attributes);
    }

    public String getId() {
        return this.attributes.id();
    }

    @Override
    public String[] getDescription() {
        return this.attributes.fileComments();
    }

    public T getDefaultValue() {
        return this.attributes.value();
    }

    public void setValueToMemory(Object value) {
        this.attributes.context().setFieldValue(value);
    }

    public void setParent(ConfigValue<?> parent) {
        this.parent = parent;
    }

    public ConfigValue<?> getParent() {
        return this.parent;
    }

    public TypeAdapter.AdapterContext getContext() {
        return this.attributes.context();
    }

    public Class<T> getValueType() {
        return this.valueType;
    }

    public TypeAdapter.TypeAttributes<T> getAttributes() {
        return this.attributes;
    }
}
