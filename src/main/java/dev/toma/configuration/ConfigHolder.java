package dev.toma.configuration;

import dev.toma.configuration.format.IConfigFormat;
import dev.toma.configuration.io.ConfigClassReader;
import dev.toma.configuration.io.ConfigLoadingContext;
import dev.toma.configuration.value.ConfigValue;
import dev.toma.configuration.value.ConfigValueIdentifier;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class ConfigHolder<T> implements Supplier<T> {

    private final ResourceLocation configId;
    private final Map<ConfigValueIdentifier, ConfigValue<?>> valueLookupMap = new HashMap<>();
    private final T config;
    private final IConfigFormat format;

    public ConfigHolder(ResourceLocation configId, T config, IConfigFormat format) {
        this.configId = configId;
        this.config = config;
        this.format = format;
        this.loadDefault();
    }

    @Override
    public T get() {
        return config;
    }

    public ResourceLocation getConfigId() {
        return configId;
    }

    public IConfigFormat getFormat() {
        return format;
    }

    public Collection<ConfigValue<?>> getValues() {
        return this.valueLookupMap.values();
    }

    private void loadDefault() {
        this.valueLookupMap.clear();
        try {
            ConfigLoadingContext context = new ConfigLoadingContext(this.configId, this.config, this.valueLookupMap, ConfigValueIdentifier.Processor.forFile(this.configId));
            ConfigClassReader reader = new ConfigClassReader(context);
            reader.loadValues();
        } catch (Exception e) {
            throw new RuntimeException("Unable to process config " + configId);
        }
    }
}
