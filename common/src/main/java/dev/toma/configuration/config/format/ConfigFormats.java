package dev.toma.configuration.config.format;

import java.util.function.Supplier;

/**
 * Collection and factory methods for config formats natively supported by
 * this library. Note that there are provided methods which allow you to
 * customize the config format, for example you can customize the GSON object
 * in for JSON configs or spacing/separators for Properties configs.
 *
 * @author Toma
 * @since 2.0
 */
public final class ConfigFormats {

    // file extensions
    private static final String EXT_JSON = "json";
    private static final String EXT_YAML = "yaml";
    private static final String EXT_PROPERTIES = "properties";

    // Formats
    /** JSON config format. Does not support comments */
    public static final IConfigFormatHandler JSON = new SimpleFormatImpl(EXT_JSON, GsonFormat::new);
    /** YAML config format. With comments */
    public static final IConfigFormatHandler YAML = new SimpleFormatImpl(EXT_YAML, YamlFormat::new);
    /** Properties config format. Does not support comments */
    public static final IConfigFormatHandler PROPERTIES = new SimpleFormatImpl(EXT_PROPERTIES, PropertiesFormat::new);

    private record SimpleFormatImpl(String fileExt, Supplier<IConfigFormat> factory) implements IConfigFormatHandler {

        @Override
        public IConfigFormat createFormat() {
            return factory.get();
        }
    }
}
