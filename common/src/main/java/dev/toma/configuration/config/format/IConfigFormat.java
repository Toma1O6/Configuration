package dev.toma.configuration.config.format;

import dev.toma.configuration.config.exception.ConfigReadException;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.value.ConfigValue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Handles exporting of data to custom file format
 *
 * @author Toma
 * @since 2.0
 */
public interface IConfigFormat {

    void writeBoolean(String field, boolean value);

    boolean readBoolean(String field) throws ConfigValueMissingException;

    void writeChar(String field, char value);

    char readChar(String field) throws ConfigValueMissingException;

    void writeByte(String field, byte value);

    byte readByte(String field) throws ConfigValueMissingException;

    void writeShort(String field, short value);

    short readShort(String field) throws ConfigValueMissingException;

    void writeInt(String field, int value);

    int readInt(String field) throws ConfigValueMissingException;

    void writeLong(String field, long value);

    long readLong(String field) throws ConfigValueMissingException;

    void writeFloat(String field, float value);

    float readFloat(String field) throws ConfigValueMissingException;

    void writeDouble(String field, double value);

    double readDouble(String field) throws ConfigValueMissingException;

    void writeString(String field, String value);

    String readString(String field) throws ConfigValueMissingException;

    void writeBoolArray(String field, Boolean[] values);

    Boolean[] readBoolArray(String field) throws ConfigValueMissingException;

    void writeCharArray(String field, Character[] values);

    Character[] readCharArray(String field) throws ConfigValueMissingException;

    void writeByteArray(String field, Byte[] values);

    Byte[] readByteArray(String field) throws ConfigValueMissingException;

    void writeShortArray(String field, Short[] values);

    Short[] readShortArray(String field) throws ConfigValueMissingException;

    void writeIntArray(String field, Integer[] values);

    Integer[] readIntArray(String field) throws ConfigValueMissingException;

    void writeLongArray(String field, Long[] values);

    Long[] readLongArray(String field) throws ConfigValueMissingException;

    void writeFloatArray(String field, Float[] values);

    Float[] readFloatArray(String field) throws ConfigValueMissingException;

    void writeDoubleArray(String field, Double[] values);

    Double[] readDoubleArray(String field) throws ConfigValueMissingException;

    void writeStringArray(String field, String[] values);

    String[] readStringArray(String field) throws ConfigValueMissingException;

    <E extends Enum<E>> void writeEnum(String field, E value);

    <E extends Enum<E>> E readEnum(String field, Class<E> enumClass) throws ConfigValueMissingException;

    <E extends Enum<E>> void writeEnumArray(String field, E[] value);

    <E extends Enum<E>> E[] readEnumArray(String field, Class<E> enumClass) throws ConfigValueMissingException;

    void writeMap(String field, Map<String, ConfigValue<?>> value);

    void readMap(String field, Collection<ConfigValue<?>> values) throws ConfigValueMissingException;

    void readFile(File file) throws IOException, ConfigReadException;

    void writeFile(File file) throws IOException;

    void addComments(String[] fileComments);

    static Boolean parseBoolean(String string) throws ConfigValueMissingException {
        try {
            return Boolean.parseBoolean(string);
        } catch (NumberFormatException e) {
            throw new ConfigValueMissingException(string);
        }
    }

    static Character parseCharacter(String string) throws ConfigValueMissingException {
        if (string.isEmpty())
            throw new ConfigValueMissingException(string);
        return string.charAt(0);
    }

    static Byte parseByte(String string) throws ConfigValueMissingException {
        try {
            return Byte.parseByte(string);
        } catch (NumberFormatException e) {
            throw new ConfigValueMissingException(string);
        }
    }

    static Short parseShort(String string) throws ConfigValueMissingException {
        try {
            return Short.parseShort(string);
        } catch (NumberFormatException e) {
            throw new ConfigValueMissingException(string);
        }
    }

    static Integer parseInteger(String string) throws ConfigValueMissingException {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            throw new ConfigValueMissingException(string);
        }
    }

    static Long parseLong(String string) throws ConfigValueMissingException {
        try {
            return Long.parseLong(string);
        } catch (NumberFormatException e) {
            throw new ConfigValueMissingException(string);
        }
    }

    static Float parseFloat(String string) throws ConfigValueMissingException {
        try {
            return Float.parseFloat(string);
        } catch (NumberFormatException e) {
            throw new ConfigValueMissingException(string);
        }
    }

    static Double parseDouble(String value) throws ConfigValueMissingException {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new ConfigValueMissingException("Could not parse double value: " + value);
        }
    }

    @FunctionalInterface
    interface ValueDecoder<T> {
        T decode(String field) throws ConfigValueMissingException;
    }
}
