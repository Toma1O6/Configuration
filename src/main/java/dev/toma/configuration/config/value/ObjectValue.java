package dev.toma.configuration.config.value;

import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import net.minecraft.network.PacketBuffer;

import java.lang.reflect.Field;
import java.util.Map;

public class ObjectValue extends ConfigValue<Map<String, ConfigValue<?>>> {

    public ObjectValue(ValueData<Map<String, ConfigValue<?>>> valueData) {
        super(valueData);
        this.get().values().forEach(value -> value.setParent(this));
    }

    @Override
    public void serialize(IConfigFormat format) {
        format.writeMap(this.getId(), this.get());
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        format.readMap(this.getId(), this.get().values());
    }

    public static final class Adapter extends TypeAdapter {

        public Adapter() {
            super("object");
        }

        @Override
        public boolean isTargetType(Class<?> type) {
            return !type.isArray();
        }

        @Override
        public ConfigValue<?> serialize(String name, String[] comments, Object value, TypeSerializer serializer, AdapterContext context) throws IllegalAccessException {
            Class<?> type = value.getClass();
            Map<String, ConfigValue<?>> map = serializer.serialize(type, value);
            return new ObjectValue(ValueData.of(name, map, context, comments));
        }

        @Override
        public void encodeToBuffer(ConfigValue<?> value, PacketBuffer buffer) {
        }

        @Override
        public Object decodeFromBuffer(ConfigValue<?> value, PacketBuffer buffer) {
            return null;
        }

        @Override
        public void setFieldValue(Field field, Object instance, Object value) throws IllegalAccessException {
            // Do not set anything, keep existing instance
        }

        @Override
        public int getPriorityIndex() {
            return Integer.MAX_VALUE;
        }
    }
}