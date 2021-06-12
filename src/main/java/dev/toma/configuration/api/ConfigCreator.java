package dev.toma.configuration.api;

import dev.toma.configuration.api.type.*;
import dev.toma.configuration.api.util.Nameable;
import dev.toma.configuration.api.util.NumberDisplayType;
import dev.toma.configuration.api.util.Restriction;
import dev.toma.configuration.example.ExampleConfig;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * ConfigCreator is for building config object structures.
 * You can create your own implementation using {@link ConfigPlugin#builder(ObjectType)}
 *
 * @author Toma
 */
@Deprecated
public interface ConfigCreator {

    /**
     * Construct and insert boolean object into config
     *
     * @param name ID of the object
     * @param value Default value
     * @param desc Comments
     * @return new instance of {@link BooleanType}
     */
    BooleanType createBoolean(String name, boolean value, String... desc);

    /**
     * Construct and insert integer object into config
     * Call {@link IntType#setDisplay(NumberDisplayType)} to change display for UI
     *
     * @param name ID of the object
     * @param value Default value
     * @param min Minimum value
     * @param max Maximum value
     * @param desc Comments
     * @return new instance of {@link IntType}
     * @throws IllegalArgumentException when max value is smaller than min value
     * @see NumberDisplayType
     */
    IntType createInt(String name, int value, int min, int max, String... desc) throws IllegalArgumentException;

    /**
     * Construct and insert integer object into config
     * Call {@link IntType#setDisplay(NumberDisplayType)} to change display for UI
     * Sets minimum value to {@link Integer#MIN_VALUE} and maximum value to {@link Integer#MAX_VALUE}
     *
     * @param name ID of the object
     * @param value Default value
     * @param desc Comments
     * @return new instance of {@link IntType}
     * @see NumberDisplayType
     */
    IntType createInt(String name, int value, String... desc);

    /**
     * Construct and insert decimal object into config
     * Call {@link DoubleType#setDisplay(NumberDisplayType)} to change display for UI
     * Call {@link DoubleType#setFormatting(DecimalFormat)} to change formatting
     *
     * @param name ID of the object
     * @param value Default value
     * @param min Minimum value
     * @param max Maximum value
     * @param desc Comments
     * @return new instance of {@link DoubleType}
     * @throws IllegalArgumentException when max value is smaller than min value
     * @see NumberDisplayType
     * @see DecimalFormat
     */
    DoubleType createDouble(String name, double value, double min, double max, String... desc) throws IllegalArgumentException;

    /**
     * Construct and insert decimal object into config
     * Call {@link DoubleType#setDisplay(NumberDisplayType)} to change display for UI
     * Call {@link DoubleType#setFormatting(DecimalFormat)} to change formatting
     * Sets minimum value to -{@link Double#MAX_VALUE} and maximum value to {@link Double#MAX_VALUE}
     *
     * @param name ID of the object
     * @param value Default value
     * @param desc Comments
     * @return new instance of {@link DoubleType}
     * @see NumberDisplayType
     * @see DecimalFormat
     */
    DoubleType createDouble(String name, double value, String... desc);

    /**
     * Construct and insert string object into config
     *
     * @param name ID of the object
     * @param value Default value
     * @param restriction Value {@link Restriction} for this type. Can be null
     * @param desc Comments
     * @return new instance of {@link StringType}
     * @throws PatternSyntaxException when supplied {@link Pattern} is invalid
     * @see Restriction
     */
    StringType createString(String name, String value, @Nullable Restriction restriction, String... desc);

    /**
     * Construct and insert string object into config
     * Sets {@link Pattern} to null = all characters are allowed
     *
     * @param name ID of the object
     * @param value Default value
     * @param desc Comments
     * @return new instance of {@link StringType}
     * @see Pattern
     */
    StringType createString(String name, String value, String... desc);

    /**
     * Construct and insert color string object into config
     * <b>Every color string must start with <i>#</i> character!</b>
     *
     * @param name ID of the object
     * @param color Color string like #FFFFFF for example
     * @param pattern Pattern for color validation, must require # character
     * @param desc Comments
     * @return new instance of {@link ColorType}
     */
    ColorType createColor(String name, String color, Pattern pattern, String... desc);

    /**
     * Construct and insert color string object into config.
     *
     * @param name ID of the object
     * @param colorRgb Color in #RRGGBB format
     * @param desc Comments
     * @return new instance of {@link ColorType}
     */
    ColorType createColorRGB(String name, String colorRgb, String... desc);

    /**
     * Construct and insert color string object into config
     *
     * @param name ID of the object
     * @param colorArgb Color in #AARRGGBB format
     * @param desc Comments
     * @return new instance of {@link ColorType}
     */
    ColorType createColorARGB(String name, String colorArgb, String... desc);

    /**
     * Construct and insert enum object into config
     * Object must be instance of {@link Nameable} <p>
     * Use {@link ConfigCreator#createArray(String, int, Nameable[], String...)}
     * with {@link Nameable.Wrapped#asArray(Object[], Function, Function)} for library defined enums
     *
     * @param name ID of the object
     * @param value Default value
     * @param desc Comments
     * @param <T> Enum type
     * @return new instance of {@link EnumType}
     * @see Nameable
     * @see Nameable.Wrapped
     */
    <T extends Enum<T> & Nameable> EnumType<T> createEnum(String name, T value, String... desc);

    /**
     * Construct and insert array object into config
     * Object must be instance of {@link Nameable} <p>
     * You can also use {@link Nameable.Wrapped#asArray(Object[], Function, Function)} for objects
     * which are defined by some library
     *
     * @param name ID of the object
     * @param value Default value
     * @param values Array of possible values
     * @param desc Comments
     * @param <T> Array type
     * @return new instance of {@link FixedCollectionType}
     * @see Nameable
     * @see Nameable.Wrapped
     */
    <T extends Nameable> FixedCollectionType<T> createArray(String name, T value, T[] values, String... desc);

    /**
     * Construct and insert array object into config
     * Object must be instance of {@link Nameable} <p>
     * You can also use {@link Nameable.Wrapped#asArray(Object[], Function, Function)} for objects
     * which are defined by some library
     *
     * @param name ID of the object
     * @param initialValueIndex Default value = values[index]
     * @param values Array of possible values
     * @param desc Comments
     * @param <T> Array type
     * @return new instance of {@link FixedCollectionType}
     * @see Nameable
     * @see Nameable.Wrapped
     */
    <T extends Nameable> FixedCollectionType<T> createArray(String name, int initialValueIndex, T[] values, String... desc);

    /**
     * Construct and insert {@link List} object into config
     *
     * @param name ID of the object
     * @param entry Initial value
     * @param factory Supplier for creating new objects
     * @param desc Comments
     * @param <T> List type
     * @return new instance of {@link CollectionType}
     * @see List
     * @see Supplier
     */
    <T extends AbstractConfigType<?>> CollectionType<T> createList(String name, List<T> entry, Supplier<T> factory, String... desc);

    /**
     * Construct and insert empty {@link List} object into config
     *
     * @param name ID of the object
     * @param factory Supplier for creating new objects
     * @param desc Comments
     * @param <T> List type
     * @return new instance of {@link CollectionType}
     * @see List
     * @see Supplier
     */
    <T extends AbstractConfigType<?>> CollectionType<T> createList(String name, Supplier<T> factory, String... desc);

    /**
     * Construct and insert {@link List} object into config
     * This method provides {@link Consumer} parameter which
     * grants instant access into created object
     *
     * @param name ID of the object
     * @param factory Supplier for creating new objects
     * @param consumer {@link Consumer} containing reference to constructed object
     * @param desc Comments
     * @param <T> List type
     * @return new instance of {@link CollectionType}
     * @see List
     * @see Supplier
     * @see Consumer
     */
    <T extends AbstractConfigType<?>> CollectionType<T> createFillList(String name, Supplier<T> factory, Consumer<CollectionType<T>> consumer, String... desc);

    /**
     * Construct and insert {@link T} object into config.
     * Useful for creating subcategories
     *
     * @param object Object instance, see {@link ExampleConfig.ExampleObject} for example
     * @param <T> Type of object
     * @param plugin Your plugin implementation
     * @return new instance of {@link ObjectType}
     */
    <T extends ObjectType> T createObject(T object, ConfigPlugin plugin);

    /**
     * Used to assign this instance to specific {@link ObjectType}
     * @param type Type which will be assigned to this
     */
    void assignTo(ObjectType type);
}
