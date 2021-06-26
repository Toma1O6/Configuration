package dev.toma.configuration.api.client.screen;

import dev.toma.configuration.api.ModConfig;

/**
 * Interface which should be implemented on all config related UIs.
 * Detects when you exit config screen and enqueues file update.
 */
public interface IConfigurationScreen {

    /**
     * @return Mod config which is being displayed
     */
    ModConfig getConfiguration();
}
