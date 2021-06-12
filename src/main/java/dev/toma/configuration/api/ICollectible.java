package dev.toma.configuration.api;

/**
 * Interface marking that this type has multiple valid values.
 * Used on arrays, enums and collections
 * @param <T> Type of this collection
 */
public interface ICollectible<T> {

    /**
     * Collect all elements in this type into array
     * @return Array of all elements
     */
    T[] collect();
}
