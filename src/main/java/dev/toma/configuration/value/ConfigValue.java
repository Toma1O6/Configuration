package dev.toma.configuration.value;

import java.util.Objects;

public class ConfigValue<T> {

    private final ConfigValueIdentifier identifier;
    private final T defaultValue;
    // TODO value adapter
    private T storedValue;

    public ConfigValue(ConfigValueIdentifier identifier, T defaultValue) {
        this.identifier = identifier;
        this.defaultValue = defaultValue;
        this.storedValue = defaultValue;
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
}
