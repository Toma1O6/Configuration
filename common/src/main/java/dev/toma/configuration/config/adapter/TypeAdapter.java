package dev.toma.configuration.config.adapter;

import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.value.ConfigValue;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Field;
import java.util.Map;

public abstract class TypeAdapter<V> {

    public abstract ConfigValue<V> serialize(TypeAttributes<V> attributes, Object instance, TypeSerializer serializer) throws IllegalAccessException;

    public abstract void encodeToBuffer(ConfigValue<V> value, FriendlyByteBuf buffer);

    public abstract V decodeFromBuffer(ConfigValue<V> value, FriendlyByteBuf buffer);

    public void setFieldValue(Field field, Object instance, Object value) throws IllegalAccessException {
        field.set(instance, value);
    }

    @FunctionalInterface
    public interface TypeSerializer {
        Map<String, ConfigValue<?>> serialize(Class<?> type, Object instance) throws IllegalAccessException;
    }

    public interface AdapterContext {

        TypeAdapter<?> getAdapter();

        Field getOwner();

        void setFieldValue(Object value);

        default void setValue(Object value) {
        }
    }

    public record TypeAttributes<V>(String configOwner, String id, V value, TypeAdapter.AdapterContext context,
                                    Configurable.LocalizationKey localization, String[] fileComments, boolean localizeComments
    ) {

        public <R> TypeAttributes<R> child(String id, R value, TypeAdapter.AdapterContext ctx) {
            return new TypeAttributes<>(configOwner, id, value, ctx, localization, fileComments, localizeComments);
        }
    }
}
