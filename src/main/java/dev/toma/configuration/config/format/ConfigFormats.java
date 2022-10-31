package dev.toma.configuration.config.format;

import java.util.function.Supplier;

public final class ConfigFormats {

    // file extensions
    private static final String JSON = "json";
    private static final String PROPERTIES = "properties";

    // TODO yaml, toml?

    public static IConfigFormatHandler json(GsonFormat.Settings settings) {
        return new SimpleFormatImpl(JSON, () -> new GsonFormat(settings));
    }

    public static IConfigFormatHandler json() {
        return json(new GsonFormat.Settings());
    }

    public static IConfigFormatHandler properties(PropertiesFormat.Settings settings) {
        return new SimpleFormatImpl(PROPERTIES, () -> new PropertiesFormat(settings));
    }

    public static IConfigFormatHandler properties() {
        return properties(new PropertiesFormat.Settings());
    }

    private static final class SimpleFormatImpl implements IConfigFormatHandler {

        private final String extension;
        private final Supplier<IConfigFormat> factory;

        public SimpleFormatImpl(String extension, Supplier<IConfigFormat> factory) {
            this.extension = extension;
            this.factory = factory;
        }

        @Override
        public IConfigFormat createFormat() {
            return factory.get();
        }

        @Override
        public String fileExt() {
            return extension;
        }
    }
}
