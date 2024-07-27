package dev.toma.configuration.config.format;

import dev.toma.configuration.config.ConfigUtils;
import dev.toma.configuration.config.exception.ConfigReadException;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.value.ConfigValue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;

public final class PropertiesFormat implements IConfigFormat {

    public static final String DELIMITER = ",";

    private final Properties properties = new Properties();
    private final LinkedList<String> prefixes = new LinkedList<>();

    @Override
    public void writeBoolean(String field, boolean value) {
        this.write(field, String.valueOf(value));
    }

    @Override
    public boolean readBoolean(String field) throws ConfigValueMissingException {
        return this.read(field, Boolean::parseBoolean);
    }

    @Override
    public void writeChar(String field, char value) {
        this.write(field, String.valueOf(value));
    }

    @Override
    public char readChar(String field) throws ConfigValueMissingException {
        return this.read(field, s -> s.charAt(0));
    }

    @Override
    public void writeByte(String field, byte value) {
        this.write(field, String.valueOf(value));
    }

    @Override
    public byte readByte(String field) throws ConfigValueMissingException {
        return this.read(field, Byte::parseByte);
    }

    @Override
    public void writeShort(String field, short value) {
        this.write(field, String.valueOf(value));
    }

    @Override
    public short readShort(String field) throws ConfigValueMissingException {
        return this.read(field, Short::parseShort);
    }

    @Override
    public void writeInt(String field, int value) {
        this.write(field, String.valueOf(value));
    }

    @Override
    public int readInt(String field) throws ConfigValueMissingException {
        return this.read(field, Integer::parseInt);
    }

    @Override
    public void writeLong(String field, long value) {
        this.write(field, String.valueOf(value));
    }

    @Override
    public long readLong(String field) throws ConfigValueMissingException {
        return this.read(field, Long::parseLong);
    }

    @Override
    public void writeFloat(String field, float value) {
        this.write(field, String.valueOf(value));
    }

    @Override
    public float readFloat(String field) throws ConfigValueMissingException {
        return this.read(field, Float::parseFloat);
    }

    @Override
    public void writeDouble(String field, double value) {
        this.write(field, String.valueOf(value));
    }

    @Override
    public double readDouble(String field) throws ConfigValueMissingException {
        return this.read(field, Double::parseDouble);
    }

    @Override
    public void writeString(String field, String value) {
        this.write(field, value);
    }

    @Override
    public String readString(String field) throws ConfigValueMissingException {
        return this.read(field, Function.identity());
    }

    @Override
    public void writeBoolArray(String field, Boolean[] values) {
        this.writeArray(field, values);
    }

    @Override
    public Boolean[] readBoolArray(String field) throws ConfigValueMissingException {
        return this.readArray(field, Boolean[]::new, IConfigFormat::parseBoolean);
    }

    @Override
    public void writeCharArray(String field, Character[] values) {
        this.writeArray(field, values);
    }

    @Override
    public Character[] readCharArray(String field) throws ConfigValueMissingException {
        return this.readArray(field, Character[]::new, IConfigFormat::parseCharacter);
    }

    @Override
    public void writeByteArray(String field, Byte[] values) {
        this.writeArray(field, values);
    }

    @Override
    public Byte[] readByteArray(String field) throws ConfigValueMissingException {
        return this.readArray(field, Byte[]::new, IConfigFormat::parseByte);
    }

    @Override
    public void writeShortArray(String field, Short[] values) {
        this.writeArray(field, values);
    }

    @Override
    public Short[] readShortArray(String field) throws ConfigValueMissingException {
        return this.readArray(field, Short[]::new, IConfigFormat::parseShort);
    }

    @Override
    public void writeIntArray(String field, Integer[] values) {
        this.writeArray(field, values);
    }

    @Override
    public Integer[] readIntArray(String field) throws ConfigValueMissingException {
        return this.readArray(field, Integer[]::new, IConfigFormat::parseInteger);
    }

    @Override
    public void writeLongArray(String field, Long[] values) {
        this.writeArray(field, values);
    }

    @Override
    public Long[] readLongArray(String field) throws ConfigValueMissingException {
        return this.readArray(field, Long[]::new, IConfigFormat::parseLong);
    }

    @Override
    public void writeFloatArray(String field, Float[] values) {
        this.writeArray(field, values);
    }

    @Override
    public Float[] readFloatArray(String field) throws ConfigValueMissingException {
        return this.readArray(field, Float[]::new, IConfigFormat::parseFloat);
    }

    @Override
    public void writeDoubleArray(String field, Double[] values) {
        this.writeArray(field, values);
    }

    @Override
    public Double[] readDoubleArray(String field) throws ConfigValueMissingException {
        return this.readArray(field, Double[]::new, IConfigFormat::parseDouble);
    }

    @Override
    public void writeStringArray(String field, String[] values) {
        this.writeArray(field, values);
    }

    @Override
    public String[] readStringArray(String field) throws ConfigValueMissingException {
        return this.getStringArray(field);
    }

    @Override
    public <E extends Enum<E>> void writeEnum(String field, E value) {
        this.write(field, value.name());
    }

    @Override
    public <E extends Enum<E>> E readEnum(String field, Class<E> enumClass) throws ConfigValueMissingException {
        return ConfigUtils.getEnumConstant(this.readString(field), enumClass);
    }

    @Override
    public <E extends Enum<E>> void writeEnumArray(String field, E[] value) {
        String[] strings = Arrays.stream(value).map(Enum::name).toArray(String[]::new);
        this.writeArray(field, strings);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Enum<E>> E[] readEnumArray(String field, Class<E> enumClass) throws ConfigValueMissingException {
        return this.readArray(field, len -> (E[]) Array.newInstance(enumClass, len), name -> ConfigUtils.getEnumConstant(name, enumClass));
    }

    @Override
    public void writeMap(String field, Map<String, ConfigValue<?>> value) {
        this.push(field);
        for (Map.Entry<String, ConfigValue<?>> entry : value.entrySet()) {
            entry.getValue().serializeValue(this);
        }
        this.pop();
    }

    @Override
    public void readMap(String field, Collection<ConfigValue<?>> values) throws ConfigValueMissingException {
        this.push(field);
        for (ConfigValue<?> value : values) {
            value.deserializeValue(this);
        }
        this.pop();
    }

    @Override
    public void readFile(File file) throws IOException, ConfigReadException {
        try (FileReader reader = new FileReader(file)) {
            this.properties.load(reader);
        }
    }

    @Override
    public void writeFile(File file) throws IOException {
        this.properties.store(new FileOutputStream(file), null);
    }

    @Override
    public void addComments(String[] fileComments) {
    }

    private String getKey(String field) {
        String prefix = this.getPrefix();
        return prefix + field;
    }

    private String getPrefix() {
        if (this.prefixes.isEmpty()) {
            return "";
        }
        return String.join(".", this.prefixes) + ".";
    }

    private void push(String field) {
        this.prefixes.addLast(field);
    }

    private void pop() {
        this.prefixes.removeLast();
    }

    private <T> T read(String s, Function<String, T> parser) throws ConfigValueMissingException {
        String key = this.getKey(s);
        String val = this.properties.getProperty(key);
        if (val == null) {
            throw new ConfigValueMissingException("Missing value " + key);
        }
        try {
            return parser.apply(val);
        } catch (Exception e) {
            throw new ConfigValueMissingException("Value parse failed", e);
        }
    }

    private void write(String field, String value) {
        String key = this.getKey(field);
        this.properties.setProperty(key, value);
    }

    private <T> void writeArray(String field, T[] values) {
        String[] asString = Arrays.stream(values).map(String::valueOf).toArray(String[]::new);
        this.writeArray(field, asString);
    }

    private void writeArray(String field, String[] values) {
        this.write(field, String.join(DELIMITER, values));
    }

    private <T> T[] readArray(String field, IntFunction<T[]> factory, IConfigFormat.ValueDecoder<T> decoder) throws ConfigValueMissingException {
        String[] strings = this.getStringArray(field);
        T[] values = factory.apply(strings.length);
        for (int i = 0; i < strings.length; i++) {
            values[i] = decoder.decode(strings[i]);
        }
        return values;
    }

    private String[] getStringArray(String field) throws ConfigValueMissingException {
        String value = this.read(field, Function.identity());
        return value.split(DELIMITER);
    }
}
