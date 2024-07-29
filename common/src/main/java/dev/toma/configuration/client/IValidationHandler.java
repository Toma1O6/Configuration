package dev.toma.configuration.client;

import dev.toma.configuration.config.validate.IValidationResult;

public interface IValidationHandler {

    void setValidationResult(IValidationResult result);

    default void setOkStatus() {
        this.setValidationResult(IValidationResult.success());
    }
}
