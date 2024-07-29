package dev.toma.configuration.config.adapter;

import dev.toma.configuration.config.value.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class TypeAdapterManager {

    private static final Map<Class<?>, TypeMapper<?, ?>> TYPE_MAPPERS = new HashMap<>();
    private static final Set<AdapterHolder<TypeAdapter<?>>> ADAPTERS = new HashSet<>();

    @SuppressWarnings("unchecked")
    public static <T> TypeAttributes<T> forType(final Class<T> type) {
        TypeAdapter<T> adapter = (TypeAdapter<T>) ADAPTERS.stream()
                .filter(entry -> entry.test(type))
                .sorted()
                .map(AdapterHolder::adapter)
                .findFirst()
                .orElse(null);
        TypeMapper<T, Object> mapper = getTypeMapper(type);
        return new TypeAttributes<>(adapter, mapper);
    }

    @SuppressWarnings("unchecked")
    public static <T> TypeMapper<T, Object> getTypeMapper(Class<T> type) {
        return (TypeMapper<T, Object>) TYPE_MAPPERS.getOrDefault(type, TypeMapper.identity());
    }

    public static <T> void registerTypeMapper(Class<T> type, TypeMapper<T, ?> mapper) {
        TYPE_MAPPERS.put(type, mapper);
    }

    public static void registerTypeAdapter(TypeMatcher matcher, TypeAdapter<?> adapter) {
        if (!ADAPTERS.add(new AdapterHolder<>(matcher, adapter))) {
            throw new IllegalArgumentException("Duplicate type matcher with id: " + matcher.getIdentifier());
        }
    }

    static {
        // mappers
        registerTypeMapper(boolean[].class, TypeMappers.boolArrayRemapper());
        registerTypeMapper(byte[].class, TypeMappers.byteArrayRemapper());
        registerTypeMapper(char[].class, TypeMappers.charArrayRemapper());
        registerTypeMapper(short[].class, TypeMappers.shortArrayRemapper());
        registerTypeMapper(int[].class, TypeMappers.intArrayRemapper());
        registerTypeMapper(long[].class, TypeMappers.longArrayRemapper());
        registerTypeMapper(float[].class, TypeMappers.floatArrayRemapper());
        registerTypeMapper(double[].class, TypeMappers.doubleArrayRemapper());
        // primitives
        registerTypeAdapter(TypeMatcher.matchBoolean(), new BooleanValue.Adapter());
        registerTypeAdapter(TypeMatcher.matchCharacter(), new CharValue.Adapter());
        registerTypeAdapter(TypeMatcher.matchByte(), new ByteValue.Adapter());
        registerTypeAdapter(TypeMatcher.matchShort(), new ShortValue.Adapter());
        registerTypeAdapter(TypeMatcher.matchInteger(), new IntValue.Adapter());
        registerTypeAdapter(TypeMatcher.matchLong(), new LongValue.Adapter());
        registerTypeAdapter(TypeMatcher.matchFloat(), new FloatValue.Adapter());
        registerTypeAdapter(TypeMatcher.matchDouble(), new DoubleValue.Adapter());
        registerTypeAdapter(TypeMatcher.matchString(), new StringValue.Adapter());

        // primitive arrays
        registerTypeAdapter(TypeMatcher.matchBooleanArray(), new BooleanArrayValue.Adapter());
        registerTypeAdapter(TypeMatcher.matchCharacterArray(), new CharArrayValue.Adapter());
        registerTypeAdapter(TypeMatcher.matchByteArray(), new ByteArrayValue.Adapter());
        registerTypeAdapter(TypeMatcher.matchShortArray(), new ShortArrayValue.Adapter());
        registerTypeAdapter(TypeMatcher.matchIntegerArray(), new IntArrayValue.Adapter());
        registerTypeAdapter(TypeMatcher.matchLongArray(), new LongArrayValue.Adapter());
        registerTypeAdapter(TypeMatcher.matchFloatArray(), new FloatArrayValue.Adapter());
        registerTypeAdapter(TypeMatcher.matchDoubleArray(), new DoubleArrayValue.Adapter());
        registerTypeAdapter(TypeMatcher.matchStringArray(), new StringArrayValue.Adapter());

        // enums
        registerTypeAdapter(TypeMatcher.matchEnum(), new EnumValue.Adapter<>());
        registerTypeAdapter(TypeMatcher.matchEnumArray(), new EnumArrayValue.Adapter<>());

        // objects
        registerTypeAdapter(TypeMatcher.matchObject(), new ObjectValue.Adapter());
    }
}
