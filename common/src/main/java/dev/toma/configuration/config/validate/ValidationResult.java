package dev.toma.configuration.config.validate;

import dev.toma.configuration.Configuration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Validation result consisting of result {@link Type} and list of message components to be displayed on GUI.
 *
 * @author Toma
 * @since 4.0
 */
public record ValidationResult(Type type, List<Component> description) {

    public static final ValidationResult SUCCESS = new ValidationResult(Type.SUCCESS, Collections.emptyList());

    public static ValidationResult success() {
        return SUCCESS;
    }

    public static ValidationResult warning(List<Component> description) {
        return new ValidationResult(Type.WARNING, description);
    }

    public static ValidationResult warning(Component description) {
        return warning(Collections.singletonList(description));
    }

    public static ValidationResult error(List<Component> description) {
        return new ValidationResult(Type.ERROR, description);
    }

    public static ValidationResult error(Component description) {
        return error(Collections.singletonList(description));
    }

    public boolean isValid() {
        return this.type.isValid();
    }

    public boolean isWarningOrError() {
        return this.type.isWarningOrError();
    }

    /**
     * Type defines how the result should be processed. Success/Warning results are processed, errors are stopped.
     */
    public enum Type {

        /** Marks that everything is okay */
        SUCCESS(0xFFE0E0E0, 0xF0030319, 0x502493E5, 0x502469E5),
        /** Marks that the value is potentionally dangerous, but does not prevent config update */
        WARNING(0xFFFFAA00, 0xF0563900, 0x50FFB200, 0x509E6900),
        /** Marks that the value is incorrect and cannot be saved */
        ERROR(0xFFFF5555, 0xF0270006, 0x50FF0000, 0x50880000);

        public final int textColor;
        public final int backgroundColor;
        public final int backgroundFadeMinColor;
        public final int backgroundFadeMaxColor;
        public final Identifier iconPath;

        Type(int textColor, int backgroundColor, int backgroundFadeMinColor, int backgroundFadeMaxColor) {
            this.textColor = textColor;
            this.backgroundColor = backgroundColor;
            this.backgroundFadeMinColor = backgroundFadeMinColor;
            this.backgroundFadeMaxColor = backgroundFadeMaxColor;
            this.iconPath = Identifier.fromNamespaceAndPath(Configuration.MODID, "textures/icons/" + this.name().toLowerCase(Locale.ROOT) + ".png");
        }

        public boolean isWarningOrError() {
            return this != SUCCESS;
        }

        public boolean isValid() {
            return this != ERROR;
        }

        public boolean isMoreSevereThan(Type other) {
            return this.ordinal() > other.ordinal();
        }

        public boolean isSameOrMoreSevereThan(Type other) {
            return this.ordinal() >= other.ordinal();
        }
    }
}
