package dev.toma.configuration.api;

import dev.toma.configuration.Configuration;
import net.minecraft.resources.ResourceLocation;

public final class TypeKey {

    public static final TypeKey BOOLEAN = newKey("boolean", ConfigSortIndexes.BOOLEAN);
    public static final TypeKey INT = newKey("int", ConfigSortIndexes.INT);
    public static final TypeKey DOUBLE = newKey("double", ConfigSortIndexes.DOUBLE);
    public static final TypeKey STRING = newKey("string", ConfigSortIndexes.STRING);
    public static final TypeKey ARRAY = newKey("array", ConfigSortIndexes.ARRAY);
    public static final TypeKey ENUM = inherit("enum", ARRAY, ConfigSortIndexes.ENUM);
    public static final TypeKey COLOR = inherit("color", STRING);
    public static final TypeKey COLLECTION = newKey("collection", ConfigSortIndexes.COLLECTION);
    public static final TypeKey OBJECT = newKey("object", ConfigSortIndexes.OBJECT);

    private final ResourceLocation key;
    private final TypeKey parent;
    private final int sortIndex;

    public static TypeKey newKey(ResourceLocation key, int sortIndex) {
        return new TypeKey(key, null, sortIndex);
    }

    public static TypeKey inherit(ResourceLocation key, TypeKey parent, int sortIndex) {
        return new TypeKey(key, parent, sortIndex);
    }

    public static TypeKey inherit(ResourceLocation key, TypeKey parent) {
        return inherit(key, parent, parent.getSortIndex());
    }

    private static TypeKey newKey(String name, int sortIndex) {
        return newKey(new ResourceLocation(Configuration.MODID, name), sortIndex);
    }

    private static TypeKey inherit(String name, TypeKey parent, int sortIndex) {
        return inherit(new ResourceLocation(Configuration.MODID, name), parent, sortIndex);
    }

    private static TypeKey inherit(String name, TypeKey parent) {
        return inherit(new ResourceLocation(Configuration.MODID, name), parent, parent.getSortIndex());
    }

    private TypeKey(ResourceLocation key, TypeKey parent, int sortIndex) {
        this.key = key;
        this.parent = parent;
        this.sortIndex = sortIndex;
    }

    public ResourceLocation getKey() {
        return key;
    }

    public boolean isChild() {
        return parent != null;
    }

    public TypeKey getParent() {
        return parent;
    }

    public int getSortIndex() {
        return sortIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeKey typeKey = (TypeKey) o;
        return key.equals(typeKey.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return "TypeKey{" +
                "key=" + key +
                '}';
    }
}
