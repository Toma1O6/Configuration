package dev.toma.configuration.config.adapter;

import dev.toma.configuration.config.value.ConfigValue;
import net.minecraft.network.PacketBuffer;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

public abstract class TypeAdapter {

    public abstract ConfigValue<?> serialize(String name, String[] comments, Object value, TypeSerializer serializer, AdapterContext context) throws IllegalAccessException;

    public abstract void encodeToBuffer(ConfigValue<?> value, PacketBuffer buffer);

    public abstract Object decodeFromBuffer(ConfigValue<?> value, PacketBuffer buffer);

    public void setFieldValue(Field field, Object instance, Object value) throws IllegalAccessException {
        field.set(instance, value);
    }

    @FunctionalInterface
    public interface TypeSerializer {
        Map<String, ConfigValue<?>> serialize(Class<?> type, Object instance) throws IllegalAccessException;
    }

    public interface AdapterContext {

        TypeAdapter getAdapter();

        Field getOwner();

        void setFieldValue(Object value);
    }
}
