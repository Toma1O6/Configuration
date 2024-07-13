package dev.toma.configuration.client;

import dev.toma.configuration.config.adapter.TypeMatcher;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public final class DisplayAdapterManager {

    private static final Map<Class<?>, Class<?>> TYPE_MAPPERS = new HashMap<>();
    private static final Map<TypeMatcher, DisplayAdapter> ADAPTER_MAP = new HashMap<>();

    public static DisplayAdapter forType(Class<?> type) {
        Class<?> mappedType = TYPE_MAPPERS.getOrDefault(type, type);
        return ADAPTER_MAP.entrySet().stream()
                .filter(entry -> entry.getKey().test(mappedType))
                .sorted(Comparator.comparingInt(value -> value.getKey().priority()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    public static void registerDisplayAdapter(TypeMatcher matcher, DisplayAdapter adapter) {
        if (ADAPTER_MAP.put(matcher, adapter) != null) {
            throw new IllegalArgumentException("Duplicate type matcher with id: " + matcher.getIdentifier());
        }
    }

    public static void registerTypeMapper(Class<?> from, Class<?> to) {
        TYPE_MAPPERS.put(from, to);
    }

    static {
        registerTypeMapper(Boolean.class, Boolean.TYPE);
        registerTypeMapper(Character.class, Character.TYPE);
        registerTypeMapper(Byte.class, Byte.TYPE);
        registerTypeMapper(Short.class, Short.TYPE);
        registerTypeMapper(Integer.class, Integer.TYPE);
        registerTypeMapper(Long.class, Long.TYPE);
        registerTypeMapper(Float.class, Float.TYPE);
        registerTypeMapper(Double.class, Double.TYPE);

        registerDisplayAdapter(TypeMatcher.matchBoolean(), DisplayAdapter.booleanValue());
        registerDisplayAdapter(TypeMatcher.matchCharacter(), DisplayAdapter.characterValue());
        registerDisplayAdapter(TypeMatcher.matchInteger(), DisplayAdapter.integerValue());
        registerDisplayAdapter(TypeMatcher.matchLong(), DisplayAdapter.longValue());
        registerDisplayAdapter(TypeMatcher.matchFloat(), DisplayAdapter.floatValue());
        registerDisplayAdapter(TypeMatcher.matchDouble(), DisplayAdapter.doubleValue());
        registerDisplayAdapter(TypeMatcher.matchString(), DisplayAdapter.stringValue());
        registerDisplayAdapter(TypeMatcher.matchBooleanArray(), DisplayAdapter.booleanArrayValue());
        registerDisplayAdapter(TypeMatcher.matchIntegerArray(), DisplayAdapter.integerArrayValue());
        registerDisplayAdapter(TypeMatcher.matchLongArray(), DisplayAdapter.longArrayValue());
        registerDisplayAdapter(TypeMatcher.matchFloatArray(), DisplayAdapter.floatArrayValue());
        registerDisplayAdapter(TypeMatcher.matchDoubleArray(), DisplayAdapter.doubleArrayValue());
        registerDisplayAdapter(TypeMatcher.matchStringArray(), DisplayAdapter.stringArrayValue());
        registerDisplayAdapter(TypeMatcher.matchEnum(), DisplayAdapter.enumValue());
        registerDisplayAdapter(TypeMatcher.matchEnumArray(), DisplayAdapter.enumArrayValue());
        registerDisplayAdapter(TypeMatcher.matchObject(), DisplayAdapter.objectValue());
    }
}
