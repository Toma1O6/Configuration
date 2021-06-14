package dev.toma.configuration.api;

import dev.toma.configuration.api.type.*;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Writer is responsible for creating config type instances and adding them to internal
 * structure map.
 *
 * @author Toma
 */
public interface IConfigWriter {

    /**
     * Write custom type into config structure
     *
     * @param configType Config type implementation
     * @param <T> Value type
     * @return Constructed config type implementation
     */
    <T extends IConfigType<?>> T write(T configType);

    /**
     * Write custom object/subcategory into config structure
     *
     * @param creator Function which will construct your custom object
     * @param name ID of this object
     * @param description Description of this object
     * @param <T> Type of object
     * @return New custom object
     */
    <T extends ObjectType> T writeObject(Function<IObjectSpec, T> creator, String name, String... description);

    /**
     * Write custom empty collection into config structure
     *
     * @param name ID of this collection
     * @param elementFactory Instance creator for new elements
     * @param description Description of this collection
     * @param <T> Type of collection
     * @return New empty custom collection
     */
    <T extends IConfigType<?>> CollectionType<T> writeList(String name, Supplier<T> elementFactory, String... description);

    /**
     * Write custom collection into config structure.
     * Also inserts supplied list of elements into this. <b>New list is not created!</b>.
     * Which means that all operations run on this list instance will also affect internal list of this collection.
     *
     * @param name ID of this collection
     * @param list List reference
     * @param elementFactory Instance creator for new elements
     * @param description Description of this collection
     * @param <T> Type of collection
     * @return New custom collection with external list reference
     */
    <T extends IConfigType<?>> CollectionType<T> writeFillList(String name, List<T> list, Supplier<T> elementFactory, String... description);

    /**
     * Write custom empty collection into config structure and run extra logic on it.
     *
     * @param name ID of this collection
     * @param elementFactory Instance creator for new elements
     * @param action Action which will be run after object initialization
     * @param description Description of this collection
     * @param <T> Type of collection
     * @return New custom collection
     */
    <T extends IConfigType<?>> CollectionType<T> writeApplyList(String name, Supplier<T> elementFactory, Consumer<List<T>> action, String... description);

    /**
     * Write array into config structure.
     * <p> Implement {@link INameable} on array type to have better control over display names.
     * If you don't want to implement that interface on your type, you must atleast override toString method
     * otherwise this element won't work correctly.
     *
     * @param name ID of this array
     * @param selectedValue Value which will be selected as default
     * @param values Array of nonnull valid values
     * @param description Description of this array
     * @param <T> Type of array
     * @return New array
     */
    <T extends IConfigType<?>> ArrayType<T> writeArray(String name, T selectedValue, T[] values, String... description);

    /**
     * Write array into config structure.
     * <p> Implement {@link INameable} on array type to have better control over display names.
     * If you don't want to implement that interface on your type, you must atleast override toString method
     * otherwise this element won't work correctly.
     *
     * @param name ID of this array
     * @param selectedIndex Index of element which will be selected as default
     * @param values Array of nonnull valid values
     * @param description Description of this array
     * @param <T> Type of array
     * @return New array
     */
    <T extends IConfigType<?>> ArrayType<T> writeArray(String name, int selectedIndex, T[] values, String... description);

    /**
     * Write enum into config structure.
     * <p> You can implement {@link INameable} interface which will allow nicer display names in UI
     *
     * @param name ID of this enum
     * @param selectedValue Default value
     * @param description Description of this enum
     * @param <T> Type of enum
     * @return New enum type
     */
    <T extends Enum<T>> EnumType<T> writeEnum(String name, T selectedValue, String... description);

    /**
     * Write custom color type into config structure
     *
     * @param name Name of this color
     * @param color Default value
     * @param restriction Restricts invalid inputs
     * @param description Description of this color
     * @return New color type
     */
    ColorType writeColor(String name, String color, IRestriction<String> restriction, String... description);

    /**
     * Write color in RGB format into config structure
     *
     * @param name Name of this color
     * @param color Default value in RGB format
     * @param desc Description of this color
     * @return New color type
     */
    ColorType writeColorRGB(String name, String color, String... desc);

    /**
     * Write color in ARGB format into config structure
     *
     * @param name Name of this color
     * @param color Default value in ARGB format
     * @param desc Description of this color
     * @return New color type
     */
    ColorType writeColorARGB(String name, String color, String... desc);

    /**
     * Write string into config structure.
     *
     * @param name Name of this string
     * @param value Default value
     * @param desc Description of this string
     * @return New string type
     */
    StringType writeString(String name, String value, String... desc);

    /**
     * Write restricted string into config structure
     *
     * @param name Name of this string
     * @param value Default value
     * @param restriction Input restriction
     * @param desc Description of this string
     * @return New string type
     */
    StringType writeRestrictedString(String name, String value, IRestriction<String> restriction, String... desc);

    /**
     * Write double into config structure
     *
     * @param name ID of this number
     * @param value Default value
     * @param desc Description of this number
     * @return New double type
     */
    DoubleType writeDouble(String name, double value, String... desc);

    /**
     * Write bounded double into config structure
     *
     * @param name ID of this number
     * @param value Default value
     * @param min Lowest bound value
     * @param max Highest bound value
     * @param desc Description of this number
     * @return New double type
     */
    DoubleType writeBoundedDouble(String name, double value, double min, double max, String... desc);

    /**
     * Write integer into config structure
     *
     * @param name ID of this number
     * @param value Default value
     * @param desc Description of this number
     * @return New int type
     */
    IntType writeInt(String name, int value, String... desc);

    /**
     * Write bounded integer into config structure
     *
     * @param name ID of this number
     * @param value Default value
     * @param min Lowest bound value
     * @param max Highest bound value
     * @param desc Description of this number
     * @return New int type
     */
    IntType writeBoundedInt(String name, int value, int min, int max, String... desc);

    /**
     * Write boolean into config structure
     *
     * @param name ID of this boolean
     * @param value Default value
     * @param desc Description of this boolean
     * @return New boolean type
     */
    BooleanType writeBoolean(String name, boolean value, String... desc);

    /**
     * Set object which will hold config structure.
     * Called internally, <b>DO NOT CALL</b>
     * @param type Structure holder
     */
    void setWritingObject(ObjectType type);
}
