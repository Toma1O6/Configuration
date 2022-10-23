package dev.toma.configuration.value;

import dev.toma.configuration.io.ITypeAdapter;

import java.util.Objects;

public class ConfigValue<T> {

    private final ConfigValueIdentifier identifier;
    private final ITypeAdapter<T> adapter;
    private final T defaultValue;
    // TODO value adapter
    private T storedValue;

    public ConfigValue(ConfigValueIdentifier identifier, ITypeAdapter<T> adapter, T defaultValue) {
        this.identifier = identifier;
        this.adapter = adapter;
        this.defaultValue = defaultValue;
        this.storedValue = defaultValue;
    }

    public T getStoredValue() {
        return storedValue;
    }

    public ConfigValueIdentifier getIdentifier() {
        return identifier;
    }

    public ITypeAdapter<T> getAdapter() {
        return adapter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigValue<?> that = (ConfigValue<?>) o;
        return identifier.equals(that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return this.storedValue.toString();
    }
}
