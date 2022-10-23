package dev.toma.configuration.format;

import java.io.File;
import java.io.IOException;

public interface IFormattedWriter {

    void writeBoolean(String field, boolean value);

    void writeByte(String field, byte value);

    void writeShort(String field, short value);

    void writeInt(String field, int value);

    void writeLong(String field, long value);

    void writeFloat(String field, float value);

    void writeDouble(String field, double value);

    void writeChar(String field, char value);

    void writeString(String field, String value);

    void writeIntoFile(File file) throws IOException;
}
