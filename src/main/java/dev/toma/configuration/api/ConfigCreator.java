package dev.toma.configuration.api;

import dev.toma.configuration.api.type.*;
import dev.toma.configuration.api.util.Nameable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public interface ConfigCreator {

    BooleanType createBoolean(String name, boolean value, String... desc);

    IntType createInt(String name, int value, int min, int max, String... desc);

    IntType createInt(String name, int value, String... desc);

    DoubleType createDouble(String name, double value, double min, double max, String... desc);

    DoubleType createDouble(String name, double value, String... desc);

    StringType createString(String name, String value, Pattern pattern, String... desc);

    StringType createString(String name, String value, String... desc);

    <T extends Enum<T> & Nameable> EnumType<T> createEnum(String name, T value, String... desc);

    <T extends Nameable> FixedCollectionType<T> createArray(String name, T value, T[] values, String... desc);

    <T extends Nameable> FixedCollectionType<T> createArray(String name, int initialValueIndex, T[] values, String... desc);

    <T extends AbstractConfigType<?>> CollectionType<T> createList(String name, List<T> entry, Supplier<T> factory, String... desc);

    <T extends AbstractConfigType<?>> CollectionType<T> createList(String name, Supplier<T> factory, String... desc);

    <T extends AbstractConfigType<?>> CollectionType<T> createFillList(String name, Supplier<T> factory, Consumer<CollectionType<T>> consumer, String... desc);

    <T extends ObjectType> T createObject(T object);

    void assignTo(ObjectType type);
}
