package dev.toma.configuration.exception;

public class ConfigLoadingException extends Exception {

    public ConfigLoadingException(String cause) {
        super(cause);
    }

    public ConfigLoadingException(Throwable cause) {
        super(cause);
    }
}
