package dev.toma.configuration.config.exception;

public class ConfigReadException extends Exception {

    public ConfigReadException() {
        super();
    }

    public ConfigReadException(String message) {
        super(message);
    }

    public ConfigReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigReadException(Throwable cause) {
        super(cause);
    }
}
