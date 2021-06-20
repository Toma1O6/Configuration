package dev.toma.configuration.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Skeleton for all config types.
 *
 * @param <T> Type of this config type
 * @author Toma
 */
public interface IConfigType<T> {

    TypeKey getType();

    /**
     * Get ID of this config type.
     * Used as key in {@link java.util.Map}s
     * @return ID of this config type
     */
    String getId();

    /**
     * Get description of this config type.
     * Useful for providing user with more detailed description.
     * @return Comments provided on this type
     */
    String[] getComments();

    /**
     * You can use this to add extra description lines into already existing description
     */
    void generateComments();

    /**
     * @return Value of this type
     */
    T get();

    /**
     * Set value for this type
     * @param t New value
     */
    void set(T t);

    /**
     * Loads value from json element which is saved under
     * value key inside this config type's JsonObject.
     *
     * @param element Element which contains all value data
     * @return Loaded value
     * @throws JsonParseException When value is invalid
     */
    T load(JsonElement element) throws JsonParseException;

    /**
     * Saves value into JsonObject which is assigned to this type.
     *
     * @param isUpdate Whether this is trigerred by update event or not.
     *                 {@code isUpdate} will be false only during initial load.
     *                 Acts as control for description generation
     *
     * @return JsonElement containing all necessary data needed for reconstruction of saved value
     */
    JsonElement save(boolean isUpdate);

    /**
     * Loads all data related to config type from JsonObject structure.
     * Structure scheme:
     *
     * TypeID: {
     *     "comments": [array], --Comments don't have to be always present
     *     "value": X
     * }
     *
     * You shouldn't need to load comments from the json as it is generated
     * during config type construction.
     * All you care about is the {@code value} element.
     * You can parse it's value using the {@link IConfigType#load(JsonElement)} method
     *
     * @param object Object assigned to this type
     * @throws JsonParseException When invalid value is encountered during parsing
     */
    void loadData(JsonObject object) throws JsonParseException;

    /**
     * Saves all data related to config type into JsonObject structure
     * Default scheme is:
     * TypeID: {
     *     "comments": [array], --optional
     *     "value": X
     * }
     *
     * Names above are what is used by default types hovewer you may use whatever scheme fits your needs
     *
     * @param element Parent element of this type. Write your JsonObject into it
     * @param isUpdate Whether this is trigerred by update event or not.
     *                 {@code isUpdate} will be false only during initial load.
     *                 Acts as control for description generation
     */
    void saveData(JsonElement element, boolean isUpdate);

    /**
     * Set your custom index based on "where" this component should appear in UI.
     * <p> You can see default sort indexes in {@link ConfigSortIndexes} file.
     * @return Sort index of this type
     * @deprecated Will be removed in future version. Use {@link TypeKey#getSortIndex()} instead
     */
    @Deprecated
    int getSortIndex();
}
