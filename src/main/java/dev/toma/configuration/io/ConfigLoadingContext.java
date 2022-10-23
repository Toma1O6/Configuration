package dev.toma.configuration.io;

import dev.toma.configuration.value.ConfigValue;
import dev.toma.configuration.value.ConfigValueIdentifier;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public final class ConfigLoadingContext {

    private final ResourceLocation file;
    private final Class<?> cfgClass;
    private final Object instance;
    private final Map<ConfigValueIdentifier, ConfigValue<?>> map;
    private final ConfigValueIdentifier.Processor processor;

    public ConfigLoadingContext(ResourceLocation file, Object instance, Map<ConfigValueIdentifier, ConfigValue<?>> map, ConfigValueIdentifier.Processor processor) {
        this.file = file;
        this.cfgClass = instance.getClass();
        this.instance = instance;
        this.map = map;
        this.processor = processor;
    }

    public ResourceLocation getFile() {
        return file;
    }

    public Class<?> getCfgClass() {
        return cfgClass;
    }

    public Object getInstance() {
        return instance;
    }

    public Map<ConfigValueIdentifier, ConfigValue<?>> getMap() {
        return map;
    }

    public ConfigValueIdentifier.Processor getProcessor() {
        return processor;
    }
}
