package dev.toma.configuration.config.format;

public interface IConfigFormatHandler {

    IConfigFormat createFormat();

    String fileExt();
}
