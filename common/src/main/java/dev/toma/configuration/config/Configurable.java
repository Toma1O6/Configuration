package dev.toma.configuration.config;

import dev.toma.configuration.config.validate.RequirementCondition;
import org.intellij.lang.annotations.RegExp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for field to config serialization.
 * Only public instance fields are allowed.
 *
 * @author Toma
 * @since 2.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Configurable {

    /**
     * Allows you to specify how localization key will be generated. Your choices are either
     * {@link LocalizationKey#SHORT} - old behaviour, only the field name is used for translation key
     * {@link LocalizationKey#FULL} - For nested fields will prefix field names of all parent values
     * @return {@link LocalizationKey} to be used by this field. By default, {@link LocalizationKey#SHORT} is used.
     * @since 3.0
     */
    LocalizationKey key() default LocalizationKey.SHORT;

    /**
     * Allows specification of custom translation keys, may be useful for reusable config objects for example.
     *
     * @return Custom translation key to be used for this field
     * @since 3.2.0
     */
    String value() default "";

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

        /**
         * Setting comment localizations to {@code true} will force generation of translation keys for each element from
         * {@link Comment#value()} array. The translation keys for comments are in following format: {@code <valueLanguageKey>.comment.<index>}.
         * So for example for field {@code myConfigField} with single comment will generate following language key:
         * {@code config.config_id.option.myConfigField.comment.0} <br>
         * When disabled, non-translated comments from {@link Comment#value()} array will be used instead. <br>
         * Default value {@code true} used since {@code 3.3.0} version
         *
         * @return {@code true} if localized comments should be generated
         * @since 3.0
         */
        boolean localize() default true;
    }

    /**
     * Field values annotated by this will be automatically
     * synchronized to client when joining server.
     * Does not rewrite client config file, all values
     * are recovered when leaving server.
     * Beware that this cannot be used along with {@linkplain UpdateRestrictions#GAME_RESTART}
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Synchronized {
    }

    /**
     * Allows you to add update restrictions/warnings, as some values may for example require
     * game restart or leaving current server.
     *
     * @since 3.0
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface UpdateRestriction {
        UpdateRestrictions value();
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
        @RegExp
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
     * Allows you to restrict edit mode behind certain condition. Does not actually restrict value change, but should be
     * rather used to indicate to end user that this value currently does not have any functionality, such as cases
     * when the annotated config field functionality is blocked by some other config property. For example {@code craftTime}
     * config value could be restricted by {@code craftingEnabled} config value.
     *
     * @since 4.0
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Requires {

        /**
         * Makes value dependent on specified mod being loaded
         * @return array of mods on which this config value depends on
         */
        ActiveMod[] mods() default {};

        /**
         * Makes value dependent
         * @return
         */
        ConfigValue[] configValues() default {};

        CustomCondition[] conditions() default {};

        LogicalOperator operator() default LogicalOperator.AND;

        @interface ActiveMod {
            String value();
            boolean invert() default false;
        }

        @interface ConfigValue {
            String config();
            String path();
            String[] accepts();
            boolean invert() default false;
        }

        @interface CustomCondition {
            Class<? extends RequirementCondition> value();
        }
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

        /**
         * Adds color display next to your string value
         */
        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface ColorValue {

            /**
             * @return If your value supports alpha values, otherwise will always be rendered as solid color
             */
            boolean isARGB() default false;

            String getGuiColorPrefix() default "#";
        }

        /**
         * Allows you to change character limit for text fields
         */
        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface CharacterLimit {

            /**
             * @return Character limit to be used by text field
             */
            int value() default 32;
        }

        /**
         * Allows you to mark current config field as slider. Applicable only on Numeric config values.
         * @since 3.0
         */
        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface Slider {
        }

        /**
         * Allows you to specify field visibility in GUI. Can be useful for some more advanced options which shouldn't
         * be accessed by all users, or you can completely hide some options from GUI
         * @since 3.1
         */
        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface Visibility {

            /**
             * @return Specified field visibility to be set on the field. {@link FieldVisibility#NORMAL} is used by default.
             */
            FieldVisibility value();
        }
    }

    /**
     * Localization key format specification for config values
     *
     * @since 3.0
     */
    enum LocalizationKey {

        /** Generates full translation key - meaning all parent config field names are used as prefix for the translation key */
        FULL,

        /**
         * Generates partial translation key - only current field name is used for translation key.
         * This can simplify writing language entries for shared fields
         */
        SHORT
    }
}
