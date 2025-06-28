package dev.toma.configuration.config.util;

import dev.toma.configuration.config.value.IConfigValueReadable;

/**
 * Allows you to listen for changes within specific config values.
 * @param <V> The config value type
 */
@FunctionalInterface
public interface ValueListener<V> {

    /**
     * Triggered on every value change, such as config loading, saving, updating, network update and so on.
     * @param value The config value holder.
     * @param newValue Value which is being set into the holder.
     */
    void onValueChanged(IConfigValueReadable<V> value, V newValue);
}
