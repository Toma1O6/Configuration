package dev.toma.configuration.config;

import dev.toma.configuration.config.io.ConfigIO;

/**
 * @since 3.0
 */
public enum UpdateRestrictions {

    /**
     * Value can be updated anytime
     */
    NONE,

    /**
     * Value can be updated only when user is in main menu.
     * Automatically applied for all {@link dev.toma.configuration.config.Configurable.Synchronized} fields
     */
    MAIN_MENU,

    /**
     * Value update will be only applied after game restart.
     * <b>Cannot be combined with {@link dev.toma.configuration.config.Configurable.Synchronized} annotation</b>
     */
    GAME_RESTART;

    public boolean canApplyChangeInEnvironment(ConfigIO.ConfigEnvironment environment) {
        return switch (this) {
            case NONE -> true;
            case MAIN_MENU -> environment != ConfigIO.ConfigEnvironment.PLAYING;
            case GAME_RESTART -> environment == ConfigIO.ConfigEnvironment.LOADING;
        };
    }

    public boolean isEditableInEnvironment(ConfigIO.ConfigEnvironment environment) {
        return switch (this) {
            case NONE -> true;
            case MAIN_MENU, GAME_RESTART -> environment != ConfigIO.ConfigEnvironment.PLAYING;
        };
    }
}
