package dev.toma.configuration.config.format;

import com.google.gson.*;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.ICommentsProvider;
import dev.toma.configuration.exception.ConfigReadException;
import dev.toma.configuration.exception.ConfigValueMissingException;
import dev.toma.configuration.io.ConfigIO;

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
    public void writeChar(String field, char value) {
        this.root.addProperty(field, value);
    }

    @Override
    public char readChar(String field) throws ConfigValueMissingException {
        return this.tryRead(field, JsonElement::getAsCharacter);
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
    public void writeString(String field, String value) {
        this.root.addProperty(field, value);
    }

    @Override
    public String readString(String field) throws ConfigValueMissingException {
        return this.tryRead(field, JsonElement::getAsString);
    }

    @Override
    public void writeIntArray(String field, int[] values) {
        JsonArray array = new JsonArray();
        for (int i : values) {
            array.add(i);
        }
        this.root.add(field, array);
    }

    @Override
    public int[] readIntArray(String field) throws ConfigValueMissingException {
        Integer[] boxed = this.readArray(field, Integer[]::new, JsonElement::getAsInt);
        int[] primitive = new int[boxed.length];
        int i = 0;
        for (int v : boxed) {
            primitive[i++] = v;
        }
        return primitive;
    }

    @Override
    public <E extends Enum<E>> void writeEnum(String field, E value) {
        this.root.addProperty(field, value.name());
    }

    @Override
    public <E extends Enum<E>> E readEnum(String field, Class<E> enumClass) throws ConfigValueMissingException {
        String value = readString(field);
        E[] constants = enumClass.getEnumConstants();
        for (E e : constants) {
            if (e.name().equals(value)) {
                return e;
            }
        }
        throw new ConfigValueMissingException("Missing enum value: " + value);
    }

    @Override
    public void writeMap(String field, Map<String, ConfigValue<?>> value) {
        GsonConfig config = new GsonConfig();
        value.values().forEach(val -> val.serializeValue(config));
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

    @Override
    public void addComments(ICommentsProvider provider) {
        // comments are not supported for JSON4 files
    }

    private <T> T tryRead(String field, Function<JsonElement, T> function) throws ConfigValueMissingException {
        JsonElement element = this.root.get(field);
        if (element == null) {
            throw new ConfigValueMissingException("Missing value: " + field);
        }
        try {
            return function.apply(element);
        } catch (Exception e) {
            Configuration.LOGGER.error(ConfigIO.MARKER, "Error loading value for field {} - {}", field, e);
            throw new ConfigValueMissingException("Invalid value");
        }
    }

    private <T> T[] readArray(String field, Function<Integer, T[]> arrayFactory, Function<JsonElement, T> function) throws ConfigValueMissingException {
        JsonElement element = this.root.get(field);
        if (element == null || !element.isJsonArray()) {
            throw new ConfigValueMissingException("Missing value: " + field);
        }
        JsonArray array = element.getAsJsonArray();
        T[] arr = arrayFactory.apply(array.size());
        try {
            int j = 0;
            for (JsonElement el : array) {
                arr[j++] = function.apply(el);
            }
            return arr;
        } catch (Exception e) {
            Configuration.LOGGER.error(ConfigIO.MARKER, "Error loading value for field {} - {}", field, e);
            throw new ConfigValueMissingException("Invalid value");
        }
    }
}
