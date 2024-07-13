package dev.toma.configuration.client;

import dev.toma.configuration.config.validate.ValidationResult;

@Deprecated
public interface IValidationHandler {

    void setValidationResult(ValidationResult result);

    default void setOkStatus() {
        this.setValidationResult(ValidationResult.ok());
    }
}
