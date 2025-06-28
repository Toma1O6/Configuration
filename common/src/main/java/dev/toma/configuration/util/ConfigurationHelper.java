package dev.toma.configuration.util;

import org.intellij.lang.annotations.RegExp;

/**
 * Collection of common utility methods/constants which may be useful for everyone.
 *
 * @since 3.4.0
 * @author Toma
 */
public final class ConfigurationHelper {

    /**
     * Regexp for RGB color defined as a hexadecimal string value in {@code #RRGGBB} format.
     * Allows values such as {@code #12}, {@code #22FFCC}
     */
    @RegExp
    public static final String SIMPLE_RGB_PATTERN = "^#[0-9a-fA-F]{1,6}$";
    /**
     * Regexp for ARGB color defined as a hexadecimal string value in {@code #AARRGGBB} format.
     * Allows values such as {@code #12}, {@code #FF22FFCC}
     */
    @RegExp
    public static final String SIMPLE_ARGB_PATTERN = "^#[0-9a-fA-F]{1,8}$";
    /**
     * Regexp for {@link net.minecraft.resources.ResourceLocation} values.
     * Namespace is optional and typically defaults to {@code minecraft}
     */
    @RegExp
    public static final String IDENTIFIER_PATTERN = "^(?:[a-z0-9_.-]+:)?[a-z0-9_./-]+$";
}
