package dev.toma.configuration.format;

import com.google.gson.*;
import dev.toma.configuration.value.ConfigValue;
import dev.toma.configuration.value.ConfigValueIdentifier;
import dev.toma.configuration.exception.ConfigLoadingException;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class JsonFormat implements IConfigFormat {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    @Override
    public Set<ConfigValue<?>> processFile(ResourceLocation config, File file) throws IOException, ConfigLoadingException {
        try (FileReader reader = new FileReader(file)) {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(reader);
            if (!element.isJsonObject()) {
                throw new ConfigLoadingException("Config is not an JSON object");
            }
            JsonObject object = element.getAsJsonObject();
            ConfigValueIdentifier.Processor processor = ConfigValueIdentifier.Processor.forFile(config);
            Set<ConfigValue<?>> configValues = new HashSet<>();
            processNestedObject(configValues, processor, object);
            return configValues;
        }
    }

    private static void processNestedObject(Set<ConfigValue<?>> valueStoreSet, ConfigValueIdentifier.Processor processor, JsonObject object) {
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            if (value.isJsonObject()) {
                processor.stepIn(key);
                processNestedObject(valueStoreSet, processor, value.getAsJsonObject());
                processor.stepOut();
            } else {
                ConfigValueIdentifier identifier = processor.field(key);
                ConfigValue<?> cfgValue = new ConfigValue<>(identifier, value);
                valueStoreSet.add(cfgValue);
            }
        }
    }

    @Override
    public String fileExtension() {
        return "json";
    }
}
