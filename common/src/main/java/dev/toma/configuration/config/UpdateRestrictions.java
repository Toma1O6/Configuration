package dev.toma.configuration.config;

import dev.toma.configuration.config.io.ConfigIO;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.Locale;

/**
 * Collection of UpdateRestrictions which can be applied on config values
 *
 * @author Toma
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

    private final Component label;

    UpdateRestrictions() {
        this.label = Component.translatable("text.configuration.description.restriction." + this.name().toLowerCase(Locale.ROOT)).withStyle(ChatFormatting.DARK_GRAY);
    }

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

    public boolean isRestricted() {
        return this != NONE;
    }

    public Component getLabel() {
        return label;
    }
}
