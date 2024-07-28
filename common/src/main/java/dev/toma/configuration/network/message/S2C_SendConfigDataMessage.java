package dev.toma.configuration.network.message;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.value.ConfigValue;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;

public record S2C_SendConfigDataMessage(String config, Map<String, NetworkConfigValue<?>> values) implements CustomPacketPayload {

    public static final ResourceLocation IDENTIFIER = ResourceLocation.fromNamespaceAndPath(Configuration.MODID, "send_config_data");
    public static final Type<S2C_SendConfigDataMessage> TYPE = new Type<>(IDENTIFIER);
    public static final StreamCodec<FriendlyByteBuf, S2C_SendConfigDataMessage> CODEC = StreamCodec.of(
            (o, s2CSendConfigDataMessage) -> s2CSendConfigDataMessage.encode(o),
            S2C_SendConfigDataMessage::decode
    );

    public S2C_SendConfigDataMessage(String config) {
        this(config, null);
    }

    private static Map<String, ConfigValue<?>> loadValuesForSynchronization(String config) {
        ConfigHolder<?> holder = ConfigHolder.getConfig(config)
                .orElseThrow(() -> new IllegalArgumentException("Unknown config: " + config));
        return holder.getNetworkSerializedFields();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private void encode(FriendlyByteBuf buffer) {
        Map<String, ConfigValue<?>> synchronizedFields = loadValuesForSynchronization(this.config);

        buffer.writeUtf(this.config);
        buffer.writeInt(synchronizedFields.size());
        for (Map.Entry<String, ConfigValue<?>> entry : synchronizedFields.entrySet()) {
            String field = entry.getKey();
            ConfigValue<?> value = entry.getValue();
            buffer.writeUtf(field);
            this.encodeToBuffer(value, buffer);
        }
    }

    private <T> void encodeToBuffer(ConfigValue<T> value, FriendlyByteBuf buffer) {
        TypeAdapter<T> adapter = value.getAdapter();
        adapter.encodeToBuffer(value, buffer);
    }

    private static S2C_SendConfigDataMessage decode(FriendlyByteBuf buffer) {
        String config = buffer.readUtf();
        int valuesCount = buffer.readInt();
        Map<String, ConfigValue<?>> synchronizedFields = loadValuesForSynchronization(config);
        if (valuesCount != synchronizedFields.size()) {
            throw new IllegalArgumentException("Number of synchronization fields did not match for config " + config);
        }
        Map<String, NetworkConfigValue<?>> values = new LinkedHashMap<>();
        for (int i = 0; i < valuesCount; i++) {
            String field = buffer.readUtf();
            ConfigValue<?> configValue = synchronizedFields.get(field);
            if (configValue == null) {
                Configuration.LOGGER.fatal("Received unknown config value {}", field);
                throw new RuntimeException("Unknown config field: " + field);
            }
            saveValue(values, configValue, field, buffer);

        }
        return new S2C_SendConfigDataMessage(config, values);
    }

    private static <T> void saveValue(Map<String, NetworkConfigValue<?>> map, ConfigValue<T> value, String field, FriendlyByteBuf buffer) {
        TypeAdapter<T> adapter = value.getAdapter();
        T t = adapter.decodeFromBuffer(value, buffer);
        map.put(field, new NetworkConfigValue<>(value, t));
    }

    public void receive() {
        for (Map.Entry<String, NetworkConfigValue<?>> entry : this.values.entrySet()) {
            NetworkConfigValue<?> value = entry.getValue();
            value.bind();
        }
    }

    private record NetworkConfigValue<T>(ConfigValue<T> configValue, T value) {

        void bind() {
            this.configValue.setFromNetwork(this.value);
        }
    }
}
