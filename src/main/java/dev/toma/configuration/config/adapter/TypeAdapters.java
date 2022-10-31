package dev.toma.configuration.config.adapter;

import dev.toma.configuration.config.value.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class TypeAdapters {

    private static final List<TypeAdapter> TYPE_ADAPTERS = new ArrayList<>();

    public static TypeAdapter getTypeAdapter(Class<?> type) {
        for (TypeAdapter adapter : TYPE_ADAPTERS) {
            if (adapter.isTargetType(type)) {
                return adapter;
            }
        }
        return null;
    }

    public static void registerTypeAdapter(TypeAdapter adapter) {
        TYPE_ADAPTERS.add(adapter);
        TYPE_ADAPTERS.sort(Comparator.comparingInt(TypeAdapter::getPriorityIndex));
    }

    static {
        // primitives
        registerTypeAdapter(new BooleanValue.Adapter());
        registerTypeAdapter(new CharValue.Adapter());
        registerTypeAdapter(new IntValue.Adapter());
        registerTypeAdapter(new LongValue.Adapter());
        registerTypeAdapter(new FloatValue.Adapter());
        registerTypeAdapter(new DoubleValue.Adapter());
        registerTypeAdapter(new StringValue.Adapter());

        // primitive arrays
        registerTypeAdapter(new BooleanArrayValue.Adapter());
        registerTypeAdapter(new IntArrayValue.Adapter());
        registerTypeAdapter(new LongArrayValue.Adapter());
        registerTypeAdapter(new FloatArrayValue.Adapter());
        registerTypeAdapter(new DoubleArrayValue.Adapter());
        registerTypeAdapter(new StringArrayValue.Adapter());

        // enums
        registerTypeAdapter(new EnumValue.Adapter<>());

        // objects
        registerTypeAdapter(new ObjectValue.Adapter());
    }
}
