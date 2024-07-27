package dev.toma.configuration.config.format;

import com.google.gson.*;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.ConfigUtils;
import dev.toma.configuration.config.exception.ConfigReadException;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.io.ConfigIO;
import dev.toma.configuration.config.value.ConfigValue;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;

public final class GsonFormat implements IConfigFormat {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final JsonObject root;

    public GsonFormat() {
        this.root = new JsonObject();
    }

    private GsonFormat(JsonObject root) {
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
    public void writeByte(String field, byte value) {
        this.root.addProperty(field, value);
    }

    @Override
    public byte readByte(String field) throws ConfigValueMissingException {
        return this.tryRead(field, JsonElement::getAsByte);
    }

    @Override
    public void writeShort(String field, short value) {
        this.root.addProperty(field, value);
    }

    @Override
    public short readShort(String field) throws ConfigValueMissingException {
        return this.tryRead(field, JsonElement::getAsShort);
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
    public void writeLong(String field, long value) {
        this.root.addProperty(field, value);
    }

    @Override
    public long readLong(String field) throws ConfigValueMissingException {
        return this.tryRead(field, JsonElement::getAsLong);
    }

    @Override
    public void writeFloat(String field, float value) {
        this.root.addProperty(field, value);
    }

    @Override
    public float readFloat(String field) throws ConfigValueMissingException {
        return this.tryRead(field, JsonElement::getAsFloat);
    }

    @Override
    public void writeDouble(String field, double value) {
        this.root.addProperty(field, value);
    }

    @Override
    public double readDouble(String field) throws ConfigValueMissingException {
        return this.tryRead(field, JsonElement::getAsDouble);
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
    public void writeBoolArray(String field, Boolean[] values) {
        writeArray(field, values, JsonArray::add);
    }

    @Override
    public Boolean[] readBoolArray(String field) throws ConfigValueMissingException {
        return readArray(field, Boolean[]::new, JsonElement::getAsBoolean);
    }

    @Override
    public void writeCharArray(String field, Character[] values) {
        writeArray(field, values, JsonArray::add);
    }

    @Override
    public Character[] readCharArray(String field) throws ConfigValueMissingException {
        return readArray(field, Character[]::new, JsonElement::getAsCharacter);
    }

    @Override
    public void writeByteArray(String field, Byte[] values) {
        writeArray(field, values, JsonArray::add);
    }

    @Override
    public Byte[] readByteArray(String field) throws ConfigValueMissingException {
        return readArray(field, Byte[]::new, JsonElement::getAsByte);
    }

    @Override
    public void writeShortArray(String field, Short[] values) {
        writeArray(field, values, JsonArray::add);
    }

    @Override
    public Short[] readShortArray(String field) throws ConfigValueMissingException {
        return readArray(field, Short[]::new, JsonElement::getAsShort);
    }

    @Override
    public void writeIntArray(String field, Integer[] values) {
        writeArray(field, values, JsonArray::add);
    }

    @Override
    public Integer[] readIntArray(String field) throws ConfigValueMissingException {
        return readArray(field, Integer[]::new, JsonElement::getAsInt);
    }

    @Override
    public void writeLongArray(String field, Long[] values) {
        writeArray(field, values, JsonArray::add);
    }

    @Override
    public Long[] readLongArray(String field) throws ConfigValueMissingException {
        return readArray(field, Long[]::new, JsonElement::getAsLong);
    }

    @Override
    public void writeFloatArray(String field, Float[] values) {
        writeArray(field, values, JsonArray::add);
    }

    @Override
    public Float[] readFloatArray(String field) throws ConfigValueMissingException {
        return readArray(field, Float[]::new, JsonElement::getAsFloat);
    }

    @Override
    public void writeDoubleArray(String field, Double[] values) {
        writeArray(field, values, JsonArray::add);
    }

    @Override
    public Double[] readDoubleArray(String field) throws ConfigValueMissingException {
        return readArray(field, Double[]::new, JsonElement::getAsDouble);
    }

    @Override
    public void writeStringArray(String field, String[] values) {
        this.writeArray(field, values, JsonArray::add);
    }

    @Override
    public String[] readStringArray(String field) throws ConfigValueMissingException {
        return this.readArray(field, String[]::new, JsonElement::getAsString);
    }

    @Override
    public <E extends Enum<E>> void writeEnum(String field, E value) {
        this.root.addProperty(field, value.name());
    }

    @Override
    public <E extends Enum<E>> E readEnum(String field, Class<E> enumClass) throws ConfigValueMissingException {
        String value = readString(field);
        return ConfigUtils.getEnumConstant(value, enumClass);
    }

    @Override
    public <E extends Enum<E>> void writeEnumArray(String field, E[] value) {
        String[] strings = Arrays.stream(value).map(Enum::name).toArray(String[]::new);
        writeStringArray(field, strings);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Enum<E>> E[] readEnumArray(String field, Class<E> enumClass) throws ConfigValueMissingException {
        String[] strings = readStringArray(field);
        E[] arr = (E[]) Array.newInstance(enumClass, strings.length);
        for (int i = 0; i < strings.length; i++) {
            arr[i] = ConfigUtils.getEnumConstant(strings[i], enumClass);
        }
        return arr;
    }

    @Override
    public void writeMap(String field, Map<String, ConfigValue<?>> value) {
        GsonFormat config = new GsonFormat();
        value.values().forEach(val -> val.serializeValue(config));
        this.root.add(field, config.root);
    }

    @Override
    public void readMap(String field, Collection<ConfigValue<?>> values) throws ConfigValueMissingException {
        JsonElement element = this.root.get(field);
        if (element == null || !element.isJsonObject())
            throw new ConfigValueMissingException("Missing config value: " + field);
        JsonObject object = element.getAsJsonObject();
        GsonFormat config = new GsonFormat(object);
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
            try {
                JsonElement element = JsonParser.parseReader(reader);
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
    public void addComments(String[] fileComments) {
        // comments are not supported for JSON4 files
    }

    private <T> void writeArray(String field, T[] array, BiConsumer<JsonArray, T> elementConsumer) {
        JsonArray ar = new JsonArray();
        for (T t : array) {
            elementConsumer.accept(ar, t);
        }
        this.root.add(field, ar);
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

    private <T> T[] readArray(String field, IntFunction<T[]> arrayFactory, Function<JsonElement, T> function) throws ConfigValueMissingException {
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
