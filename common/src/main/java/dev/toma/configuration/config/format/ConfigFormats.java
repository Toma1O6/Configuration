package dev.toma.configuration.config.format;

import dev.toma.configuration.config.format.ini.IniFormat;
import dev.toma.configuration.config.format.json.GsonFormat;
import dev.toma.configuration.config.format.properties.PropertiesFormat;
import dev.toma.configuration.config.format.yaml.YamlFormat;

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
    private static final String EXT_YML = "yml";
    private static final String EXT_PROPERTIES = "properties";
    private static final String EXT_INI = "ini";

    // Formats
    /** JSON config format. Does not support comments */
    public static final IConfigFormatHandler JSON = new SimpleFormatImpl(EXT_JSON, GsonFormat::new);
    /** YAML config format. With comments */
    public static final IConfigFormatHandler YAML = new SimpleFormatImpl(EXT_YAML, YamlFormat::new);
    /** YAML config format. With comments */
    public static final IConfigFormatHandler YML = new SimpleFormatImpl(EXT_YML, YamlFormat::new);
    /** Properties config format. Does not support comments */
    public static final IConfigFormatHandler PROPERTIES = new SimpleFormatImpl(EXT_PROPERTIES, PropertiesFormat::new);
    /**
     * INI config format. With comments.
     * @since 4.0
     */
    public static final IConfigFormatHandler INI = new SimpleFormatImpl(EXT_INI, IniFormat::new);

    private record SimpleFormatImpl(String fileExt, Supplier<IConfigFormat> factory) implements IConfigFormatHandler {

        @Override
        public IConfigFormat createFormat() {
            return factory.get();
        }
    }
}
