package dev.toma.configuration.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for field to config serialization.
 * Only instance fields are allowed (final values too).
 *
 * <br/><br/>
 * Supported types are: <br/>
 * boolean, boolean[] <br/>
 * char <br/>
 * int, int[] <br/>
 * long, long[] <br/>
 * float, float[] <br/>
 * double, double[] <br/>
 * String, String[] <br/>
 * Enum <br/>
 * Object (For nesting) <br/>
 *
 * @author Toma
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Configurable {

    /**
     * Allows you to add description to configurable value.
     * This description will be visible on hover in GUI or as
     * comment if config file (if supported by file format)
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Comment {

        /**
         * @return Array of comments for this configurable value
         */
        String[] value();
    }

    /**
     * Field values annotated by this will be automatically
     * synchronized to client when joining server.
     * Does not rewrite client config file, all values
     * are recovered when leaving server
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Synchronized {
    }

    /**
     * Allows you to specify number range for int or long values.
     * This annotation is also applicable to int/long arrays
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Range {

        /**
         * @return Minimum allowed value for this config field
         * @throws IllegalArgumentException when minimum value is larger than maximum
         */
        long min() default Long.MIN_VALUE;

        /**
         * @return Maximum allowed value for this config field
         * @throws IllegalArgumentException when maximum value is smaller than minimum
         */
        long max() default Long.MAX_VALUE;
    }

    /**
     * Allows you to specify decimal number range for float or double values.
     * This annotation is also applicable to float/double arrays
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface DecimalRange {

        /**
         * @return Minimum allowed value for this config field
         * @throws IllegalArgumentException when minimum value is larger than maximum
         */
        double min() default -Double.MAX_VALUE;

        /**
         * @return Maximum allowed value for this config field
         * @throws IllegalArgumentException when maximum value is smaller than minimum
         */
        double max() default Double.MAX_VALUE;
    }

    /**
     * Allows you to require strings to be in specific format.
     * Useful when you for example want to use this for resource locations etc.
     * This annotation is also applicable to string arrays
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface StringPattern {

        /**
         * @return Regular expression used for value checking
         * @throws IllegalArgumentException When value is not valid regex syntax
         */
        String value();

        /**
         * This value is used only for <i>string arrays</i> in case entered value does not
         * match the regular expression.
         * @return Default value to be used when user enters invalid value
         */
        String defaultValue() default "";

        /**
         * @return Flags used for {@link java.util.regex.Pattern} object.
         * You can use for example value like {@code flags = Pattern.CASE_INSENTITIVE | Pattern.LITERAL}
         * for flag specification
         */
        int flags() default 0;

        /**
         * Gui error message when user enters invalid value
         * @return Error message to be displayed on GUI
         */
        String errorDescriptor() default "";
    }

    /**
     * Allows you to lock array size based on default provided value.
     * Applicable to all arrays.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface FixedSize {
    }

    /**
     * Group of GUI cosmetic properties
     */
    final class Gui {

        /**
         * Allows you to specify number formatting for float and double values
         * in GUI.
         */
        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface NumberFormat {

            /**
             * @return Number format according to {@link java.text.DecimalFormat}.
             * @throws IllegalArgumentException When invalid format is provided
             */
            String value();
        }
    }
}
