package dev.toma.configuration.io;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.io.adapters.*;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.util.*;

public final class ClassMatchers {

    private static final Map<ResourceLocation, ITypeMatcher<?>> MATCHER_MAP = new HashMap<>();

    public static void registerMatcher(ResourceLocation identifier, ITypeMatcher<?> matcher) {
        MATCHER_MAP.put(identifier, matcher);
    }

    @SuppressWarnings("unchecked")
    public static <T> ITypeAdapter<T> getAdapter(Field field) {
        List<ITypeMatcher<?>> list = new ArrayList<>(MATCHER_MAP.values());
        list.sort(Comparator.comparingInt(ITypeMatcher::matchingPriority));
        for (ITypeMatcher<?> matcher : list) {
            ITypeAdapter<?> adapter = matcher.findAdapter(field);
            if (adapter != null) {
                return (ITypeAdapter<T>) adapter;
            }
        }
        return null;
    }

    static {
        registerMatcher(new ResourceLocation(Configuration.MODID, "boolean"), ITypeMatcher.primitive(Boolean.TYPE, Boolean.class, new BoolAdapter()));
        registerMatcher(new ResourceLocation(Configuration.MODID, "byte"), ITypeMatcher.primitive(Byte.TYPE, Byte.class, new ByteAdapter()));
        registerMatcher(new ResourceLocation(Configuration.MODID, "short"), ITypeMatcher.primitive(Short.TYPE, Short.class, new ShortAdapter()));
        registerMatcher(new ResourceLocation(Configuration.MODID, "integer"), ITypeMatcher.primitive(Integer.TYPE, Integer.class, new IntAdapter()));
        registerMatcher(new ResourceLocation(Configuration.MODID, "long"), ITypeMatcher.primitive(Long.TYPE, Long.class, new LongAdapter()));
        registerMatcher(new ResourceLocation(Configuration.MODID, "float"), ITypeMatcher.primitive(Float.TYPE, Float.class, new FloatAdapter()));
        registerMatcher(new ResourceLocation(Configuration.MODID, "double"), ITypeMatcher.primitive(Double.TYPE, Double.class, new DoubleAdapter()));
        registerMatcher(new ResourceLocation(Configuration.MODID, "char"), ITypeMatcher.primitive(Character.TYPE, Character.class, new CharacterAdapter()));
        registerMatcher(new ResourceLocation(Configuration.MODID, "string"), ITypeMatcher.matchClass(String.class, new StringAdapter()));
        // TODO enum
        // TODO array
        // TODO object
    }
}
