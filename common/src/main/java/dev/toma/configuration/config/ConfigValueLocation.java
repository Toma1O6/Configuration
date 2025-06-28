package dev.toma.configuration.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.regex.Pattern;

/**
 * Structure similar to {@link net.minecraft.resources.ResourceLocation} where namespace represents the configId and path
 * specifies path to the desired config value. This data structure supports uppercase text, numbers and characters as
 * {@code _}, {@code -} (and {@code /} in path). <p>
 *
 * Example: {@code myConfigId:rootData/nestedData/text} <p>
 *
 * For creation of new instances you can either use the constructor or use the helper method
 * {@link ConfigValueLocation#of(String, String, String...)} for simple and safe path creation
 *
 * @param namespace
 * @param path
 * @author Toma
 * @since 4.0
 */
public record ConfigValueLocation(String namespace, String path) {

    public static final Pattern PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+:[a-zA-Z0-9/_-]+$");
    public static final String NAMESPACE_SEPARATOR = ":";
    public static final String PATH_SEPARATOR = "/";
    public static final Codec<ConfigValueLocation> CODEC = Codec.STRING.comapFlatMap(
            string -> {
                try {
                    return DataResult.success(parse(string));
                } catch (IllegalArgumentException e) {
                    return DataResult.error(() -> "Invalid location: " + string);
                }
            }, ConfigValueLocation::toString
    );
    public static final StreamCodec<ByteBuf, ConfigValueLocation> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(ConfigValueLocation::parse, ConfigValueLocation::toString);

    /**
     * Creates new ConfigValueLocation from given attributes.
     * @param namespace The configId
     * @param rootDir Initial value path component
     * @param subDirs Additional value path components if accessing nested data structures
     * @return new instance of ConfigValueLocation
     */
    public static ConfigValueLocation of(String namespace, String rootDir, String... subDirs) {
        if (subDirs.length > 0) {
            return new ConfigValueLocation(namespace, rootDir + PATH_SEPARATOR + String.join(PATH_SEPARATOR, subDirs));
        } else {
            return new ConfigValueLocation(namespace, rootDir);
        }
    }

    /**
     * Parses given string to ConfigValueLocation
     * @param location Full location with namespace and path in string format
     * @return Parsed ConfigValueLocation instance
     * @throws IllegalArgumentException in case given location is invalid
     */
    public static ConfigValueLocation parse(String location) {
        if (!PATTERN.matcher(location).matches()) {
            throw new IllegalArgumentException("Invalid config location: " + location);
        }
        String[] components = location.split(NAMESPACE_SEPARATOR, 2);
        return new ConfigValueLocation(components[0], components[1]);
    }

    @Override
    public String toString() {
        return this.namespace + NAMESPACE_SEPARATOR + this.path;
    }
}
