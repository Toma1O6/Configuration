package dev.toma.configuration.config.validate;

import dev.toma.configuration.Configuration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Validation result consisting of result {@link Severity} and list of message components to be displayed on GUI.
 *
 * @author Toma
 * @since 3.0
 */
public interface IValidationResult {

    /**
     * @return Severity of this validation result
     */
    Severity severity();

    /**
     * @return List of messages to be displayed on GUI to user
     */
    List<Component> messages();

    static IValidationResult success() {
        return ValidationResult.SUCCESS;
    }

    static IValidationResult warning(List<Component> messages) {
        return new ValidationResult(Severity.WARNING, messages);
    }

    static IValidationResult warning(Component message) {
        return warning(Collections.singletonList(message));
    }

    static IValidationResult error(List<Component> messages) {
        return new ValidationResult(Severity.ERROR, messages);
    }

    static IValidationResult error(Component message) {
        return error(Collections.singletonList(message));
    }

    /**
     * Severity defines how "critical" is the incorrect value which is being set to config.
     */
    enum Severity {

        /** Marks that everything is okay */
        NONE(0xFFFFFF, 0xF0030319, 0x502493E5, 0x502469E5),
        /** Marks that the value is potentionally dangerous, but does not prevent config update */
        WARNING(0xFFAA00, 0xF0563900, 0x50FFB200, 0x509E6900),
        /** Marks that the value is incorrect and cannot be saved */
        ERROR(0xFF5555, 0xF0270006, 0x50FF0000, 0x50880000);

        public final int textColor;
        public final int backgroundColor;
        public final int backgroundFadeMinColor;
        public final int backgroundFadeMaxColor;
        public final ResourceLocation iconPath;

        Severity(int textColor, int backgroundColor, int backgroundFadeMinColor, int backgroundFadeMaxColor) {
            this.textColor = textColor;
            this.backgroundColor = backgroundColor;
            this.backgroundFadeMinColor = backgroundFadeMinColor;
            this.backgroundFadeMaxColor = backgroundFadeMaxColor;
            this.iconPath = ResourceLocation.fromNamespaceAndPath(Configuration.MODID, "textures/icons/" + this.name().toLowerCase(Locale.ROOT) + ".png");
        }

        public boolean isWarningOrError() {
            return this != NONE;
        }

        public boolean isValid() {
            return this != ERROR;
        }

        public boolean isHigherSeverityThan(Severity other) {
            return this.ordinal() > other.ordinal();
        }
    }
}
