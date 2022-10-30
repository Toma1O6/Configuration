package dev.toma.configuration.config.adapter;

import dev.toma.configuration.config.value.BooleanValue;
import dev.toma.configuration.config.value.IntValue;
import dev.toma.configuration.config.value.ObjectValue;

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
        registerTypeAdapter(new BooleanValue.Adapter());
        registerTypeAdapter(new IntValue.Adapter());

        registerTypeAdapter(new ObjectValue.Adapter());
    }
}
