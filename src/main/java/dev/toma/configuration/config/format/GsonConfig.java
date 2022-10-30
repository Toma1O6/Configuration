package dev.toma.configuration.config.format;

import com.google.gson.*;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.exception.ConfigReadException;
import dev.toma.configuration.exception.ConfigValueMissingException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public class GsonConfig implements IConfigFormat {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting().disableHtmlEscaping().create();
    private final JsonObject root;

    public GsonConfig() {
        this.root = new JsonObject();
    }

    private GsonConfig(JsonObject root) {
        this.root = root;
    }

    @Override
    public void writeBoolean(String field, boolean value) {
        this.root.addProperty(field, value);
    }

    @Override
    public boolean readBoolean(String field) throws ConfigValueMissingException {
        return this.tryRead(field, JsonElement::getAsBoolean);
    }

    @Override
    public void writeInt(String field, int value) {
        this.root.addProperty(field, value);
    }

    @Override
    public int readInt(String field) throws ConfigValueMissingException {
        return this.tryRead(field, JsonElement::getAsInt);
    }

    @Override
    public void writeMap(String field, Map<String, ConfigValue<?>> value) {
        GsonConfig config = new GsonConfig();
        value.values().forEach(val -> val.serialize(config));
        this.root.add(field, config.root);
    }

    @Override
    public void readMap(String field, Collection<ConfigValue<?>> values) throws ConfigValueMissingException {
        JsonElement element = this.root.get(field);
        if (element == null || !element.isJsonObject())
            throw new ConfigValueMissingException("Missing config value: " + field);
        JsonObject object = element.getAsJsonObject();
        GsonConfig config = new GsonConfig(object);
        for (ConfigValue<?> value : values) {
            value.deserializeValue(config);
        }
    }

    @Override
    public void writeFile(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(GSON.toJson(this.root));
        }
    }

    @Override
    public void readFile(File file) throws IOException, ConfigReadException {
        try (FileReader reader = new FileReader(file)) {
            JsonParser parser = new JsonParser();
            try {
                JsonElement element = parser.parse(reader);
                if (!element.isJsonObject()) {
                    throw new ConfigReadException("Gson config must contain JsonObject as root element!");
                }
                JsonObject object = element.getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                    this.root.add(entry.getKey(), entry.getValue());
                }
            } catch (JsonParseException e) {
                throw new ConfigReadException("Config read failed", e);
            }
        }
    }

    private <T> T tryRead(String field, Function<JsonElement, T> function) throws ConfigValueMissingException {
        JsonElement element = this.root.get(field);
        if (element == null) {
            throw new ConfigValueMissingException("Missing value: " + field);
        }
        return function.apply(element);
    }
}
