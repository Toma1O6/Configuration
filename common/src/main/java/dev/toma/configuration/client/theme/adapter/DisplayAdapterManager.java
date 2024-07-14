package dev.toma.configuration.client.theme.adapter;

import java.util.HashMap;
import java.util.Map;

public final class DisplayAdapterManager {

    private static final Map<Class<?>, Class<?>> TYPE_MAPPERS = new HashMap<>();

    public static Class<?> mapType(Class<?> type) {
        return TYPE_MAPPERS.getOrDefault(type, type);
    }

    public static void registerTypeMapper(Class<?> from, Class<?> to) {
        if (TYPE_MAPPERS.put(from, to) != null) {
            throw new IllegalArgumentException("Duplicate type mapper for type: " + from.getCanonicalName());
        }
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
    }
}
