package dev.toma.configuration.exception;

import dev.toma.configuration.api.client.IModID;
import dev.toma.configuration.internal.FileTracker;

public class ConfigLoadDataException extends RuntimeException {

    public ConfigLoadDataException(IModID modIdProvider, Exception exception) {
        super(String.format("Config load data failed for %s on %s", modIdProvider.getModID(), exception.toString()));
    }
}
