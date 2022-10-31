package dev.toma.configuration.config.exception;

public class ConfigValueMissingException extends Exception {

    public ConfigValueMissingException() {
    }

    public ConfigValueMissingException(String message) {
        super(message);
    }

    public ConfigValueMissingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigValueMissingException(Throwable cause) {
        super(cause);
    }
}
