package dev.toma.configuration.format;

import com.google.gson.*;
import dev.toma.configuration.exception.ConfigLoadingException;
import dev.toma.configuration.value.ConfigValue;
import dev.toma.configuration.value.ConfigValueIdentifier;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class JsonFormat implements IConfigFormat {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

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

    @Override
    public IFactory<IFormattedWriter> getFormatWriterFactory() {
        return Writer::new;
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
                ConfigValue<?> cfgValue = new ConfigValue<>(identifier, null, value);
                valueStoreSet.add(cfgValue);
            }
        }
    }

    @Override
    public String fileExtension() {
        return "json";
    }

    public static final class Writer implements IFormattedWriter {

        private final JsonObject root = new JsonObject();

        @Override
        public void writeBoolean(String field, boolean value) {
            root.addProperty(field, value);
        }

        @Override
        public void writeByte(String field, byte value) {
            root.addProperty(field, value);
        }

        @Override
        public void writeShort(String field, short value) {
            root.addProperty(field, value);
        }

        @Override
        public void writeInt(String field, int value) {
            root.addProperty(field, value);
        }

        @Override
        public void writeLong(String field, long value) {
            root.addProperty(field, value);
        }

        @Override
        public void writeFloat(String field, float value) {
            root.addProperty(field, value);
        }

        @Override
        public void writeDouble(String field, double value) {
            root.addProperty(field, value);
        }

        @Override
        public void writeChar(String field, char value) {
            root.addProperty(field, value);
        }

        @Override
        public void writeString(String field, String value) {
            root.addProperty(field, value);
        }

        @Override
        public void writeIntoFile(File file) throws IOException {
            try (FileWriter writer = new FileWriter(file)) {
                String asString = GSON.toJson(root);
                writer.write(asString);
            }
        }
    }
}
