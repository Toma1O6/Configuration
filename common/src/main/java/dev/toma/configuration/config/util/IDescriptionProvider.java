package dev.toma.configuration.config.util;

import dev.toma.configuration.config.value.IConfigValueReadable;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Description providers allow you to specify custom comment generators for your config values.
 *
 * @param <T> Config value type
 * @author Toma
 * @since 3.0
 */
public interface IDescriptionProvider<T> {

    /**
     * Generates new list of messages which should be displayed on GUI when player hovers over this config value.
     *
     * @param value The config value reference
     * @return New list of messages to be displayed
     */
    List<Component> generate(IConfigValueReadable<T> value);

    /**
     * @return Whether existing description should be removed
     */
    default boolean replaceDefaultDescription() {
        return true;
    }
}
