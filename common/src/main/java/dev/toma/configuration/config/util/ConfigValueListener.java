package dev.toma.configuration.config.util;

import dev.toma.configuration.config.value.IConfigValueReadable;

/**
 * TODO
 * @param <V>
 */
public interface ConfigValueListener<V> {

    void onValueChanged(IConfigValueReadable<V> value, V newValue);
}
