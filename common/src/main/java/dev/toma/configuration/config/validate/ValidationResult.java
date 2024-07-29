package dev.toma.configuration.config.validate;

import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.Collections;
import java.util.List;

public record ValidationResult(IValidationResult.Severity severity, List<Component> messages) implements IValidationResult {

    static final ValidationResult SUCCESS = new ValidationResult(IValidationResult.Severity.NONE, CommonComponents.EMPTY);

    public ValidationResult(IValidationResult.Severity severity, List<Component> messages) {
        this.severity = severity;
        this.messages = Collections.unmodifiableList(messages);
    }

    public ValidationResult(IValidationResult.Severity severity, Component message) {
        this(severity, Collections.singletonList(message));
    }
}
