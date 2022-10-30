package dev.toma.configuration.config.format;

import java.util.function.Supplier;

public final class ConfigFormats {

    private static final IConfigFormatHandler GSON = new SimpleFormatImpl("json", GsonConfig::new);

    public static IConfigFormatHandler gson() {
        return GSON;
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
