package dev.toma.configuration.config.format.ini;

import dev.toma.configuration.config.ConfigUtils;
import dev.toma.configuration.config.exception.ConfigReadException;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.value.ConfigValue;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IniFormat implements IConfigFormat {

    private static final Pattern SECTION_PATTERN = Pattern.compile("^\\[(?<section>.+)]$");
    private static final String ROOT = "root";
    private final Map<String, IniSection> sectionMap = new LinkedHashMap<>();
    private final List<String> prefix = new LinkedList<>();
    private final List<String> descriptionBuffer = new ArrayList<>();

    public IniFormat() {
        this.sectionMap.put(ROOT, new IniSection(ROOT, new LinkedHashMap<>(), Collections.emptyList()));
    }

    @Override
    public void writeBoolean(String field, boolean value) {
        this.write(field, String.valueOf(value));
    }

    @Override
    public void writeChar(String field, char value) {
        this.write(field, String.valueOf(value));
    }

    @Override
    public void writeByte(String field, byte value) {
        this.write(field, String.valueOf(value));
    }

    @Override
    public void writeShort(String field, short value) {
        this.write(field, String.valueOf(value));
    }

    @Override
    public void writeInt(String field, int value) {
        this.write(field, String.valueOf(value));
    }

    @Override
    public void writeLong(String field, long value) {
        this.write(field, String.valueOf(value));
    }

    @Override
    public void writeFloat(String field, float value) {
        this.write(field, String.valueOf(value));
    }

    @Override
    public void writeDouble(String field, double value) {
        this.write(field, String.valueOf(value));
    }

    @Override
    public void writeString(String field, String value) {
        this.write(field, value);
    }

    @Override
    public <E extends Enum<E>> void writeEnum(String field, E value) {
        this.write(field, value.name());
    }

    @Override
    public void writeBoolArray(String field, Boolean[] values) {
        this.write(field, values, String::valueOf);
    }

    @Override
    public void writeCharArray(String field, Character[] values) {
        this.write(field, values, String::valueOf);
    }

    @Override
    public void writeByteArray(String field, Byte[] values) {
        this.write(field, values, String::valueOf);
    }

    @Override
    public void writeShortArray(String field, Short[] values) {
        this.write(field, values, String::valueOf);
    }

    @Override
    public void writeIntArray(String field, Integer[] values) {
        this.write(field, values, String::valueOf);
    }

    @Override
    public void writeLongArray(String field, Long[] values) {
        this.write(field, values, String::valueOf);
    }

    @Override
    public void writeFloatArray(String field, Float[] values) {
        this.write(field, values, String::valueOf);
    }

    @Override
    public void writeDoubleArray(String field, Double[] values) {
        this.write(field, values, String::valueOf);
    }

    @Override
    public void writeStringArray(String field, String[] values) {
        this.write(field, values, String::valueOf);
    }

    @Override
    public <E extends Enum<E>> void writeEnumArray(String field, E[] value) {
        this.write(field, value, Enum::name);
    }

    @Override
    public void writeMap(String field, Map<String, ConfigValue<?>> value) {
        this.prefix.addLast(field);
        this.getSection(); // force section init
        for (Map.Entry<String, ConfigValue<?>> entry : value.entrySet()) {
            entry.getValue().serializeValue(this);
        }
        this.prefix.removeLast();
    }

    @Override
    public boolean readBoolean(String field) throws ConfigValueMissingException {
        return this.readSingle(field, Boolean::valueOf);
    }

    @Override
    public char readChar(String field) throws ConfigValueMissingException {
        return this.readSingle(field, s -> s.charAt(0));
    }

    @Override
    public byte readByte(String field) throws ConfigValueMissingException {
        return this.readSingle(field, Byte::valueOf);
    }

    @Override
    public short readShort(String field) throws ConfigValueMissingException {
        return this.readSingle(field, Short::valueOf);
    }

    @Override
    public int readInt(String field) throws ConfigValueMissingException {
        return this.readSingle(field, Integer::valueOf);
    }

    @Override
    public long readLong(String field) throws ConfigValueMissingException {
        return this.readSingle(field, Long::valueOf);
    }

    @Override
    public float readFloat(String field) throws ConfigValueMissingException {
        return this.readSingle(field, Float::valueOf);
    }

    @Override
    public double readDouble(String field) throws ConfigValueMissingException {
        return this.readSingle(field, Double::valueOf);
    }

    @Override
    public String readString(String field) throws ConfigValueMissingException {
        return this.readSingle(field, s -> s);
    }

    @Override
    public <E extends Enum<E>> E readEnum(String field, Class<E> enumClass) throws ConfigValueMissingException {
        return ConfigUtils.getEnumConstant(this.readString(field), enumClass);
    }

    @Override
    public Boolean[] readBoolArray(String field) throws ConfigValueMissingException {
        return this.readArray(field, Boolean[]::new, Boolean::valueOf);
    }

    @Override
    public Character[] readCharArray(String field) throws ConfigValueMissingException {
        return this.readArray(field, Character[]::new, IConfigFormat::parseCharacter);
    }

    @Override
    public Byte[] readByteArray(String field) throws ConfigValueMissingException {
        return this.readArray(field, Byte[]::new, Byte::valueOf);
    }

    @Override
    public Short[] readShortArray(String field) throws ConfigValueMissingException {
        return this.readArray(field, Short[]::new, Short::valueOf);
    }

    @Override
    public Integer[] readIntArray(String field) throws ConfigValueMissingException {
        return this.readArray(field, Integer[]::new, Integer::valueOf);
    }

    @Override
    public Long[] readLongArray(String field) throws ConfigValueMissingException {
        return this.readArray(field, Long[]::new, Long::valueOf);
    }

    @Override
    public Float[] readFloatArray(String field) throws ConfigValueMissingException {
        return this.readArray(field, Float[]::new, Float::valueOf);
    }

    @Override
    public Double[] readDoubleArray(String field) throws ConfigValueMissingException {
        return this.readArray(field, Double[]::new, Double::valueOf);
    }

    @Override
    public String[] readStringArray(String field) throws ConfigValueMissingException {
        return this.readArray(field, String[]::new, String::valueOf);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Enum<E>> E[] readEnumArray(String field, Class<E> enumClass) throws ConfigValueMissingException {
        return this.readArray(field, len -> (E[]) Array.newInstance(enumClass, len), name -> ConfigUtils.getEnumConstant(name, enumClass));
    }

    @Override
    public void readMap(String field, Collection<ConfigValue<?>> values) {
        this.prefix.addLast(field);
        for (ConfigValue<?> value : values) {
            value.deserializeValue(this);
        }
        this.prefix.removeLast();
    }

    @Override
    public void addComments(String[] fileComments) {
        this.descriptionBuffer.addAll(Arrays.asList(fileComments));
    }

    @Override
    public void writeFile(File file) throws IOException {
        try (PrintWriter out = new PrintWriter(file)) {
            for (IniSection section : this.sectionMap.values()) {
                if (section.isEmpty())
                    // ignore empty sections
                    continue;

                // section description
                if (section.hasDescription()) {
                    section.description.forEach(line -> out.write("#" + line + "\n"));
                }
                // section name
                String sectionName = section.name();
                out.write("[" + sectionName + "]\n");
                // section entries
                Map<String, IniEntry> entryMap = section.values();
                for (Map.Entry<String, IniEntry> entry : entryMap.entrySet()) {
                    String entryKey = entry.getKey();
                    IniEntry iniEntry = entry.getValue();
                    // entry description
                    if (iniEntry.hasDescription()) {
                        iniEntry.description.forEach(line -> out.write("#" + line + "\n"));
                    }
                    // entry key-value pairs
                    if (iniEntry.value.isEmpty()) {
                        out.write(entryKey + "=\n");
                    } else {
                        for (String value : iniEntry.value) {
                            out.write(entryKey + "=" + value + "\n");
                        }
                    }
                }
                // section separator
                out.write("\n");
            }
        }
    }

    @Override
    public void readFile(File file) throws IOException, ConfigReadException {
        List<String> lines = Files.readAllLines(file.toPath());
        for (String line : lines) {
            if (line.isBlank() || line.startsWith("#") || line.startsWith(";")) {
                continue;
            }
            if (line.startsWith("[")) {
                // section parse
                this.prefix.clear();
                Matcher matcher = SECTION_PATTERN.matcher(line);
                if (!matcher.matches()) {
                    throw new ConfigReadException("Invalid section line: " + line);
                }
                String sectionPath = matcher.group("section");
                if (sectionPath.equals(ROOT)) {
                    continue;
                }
                String[] subsections = sectionPath.split("\\.");
                this.prefix.addAll(Arrays.asList(subsections));
            } else {
                String[] keyValuePair = line.split("=", 2);
                if (keyValuePair.length != 2) {
                    throw new ConfigReadException("Invalid config entry: " + line);
                }
                IniSection section = this.getSection();
                String key = keyValuePair[0].trim();
                IniEntry entry = section.values.computeIfAbsent(key, v -> new IniEntry(new ArrayList<>(), Collections.emptyList()));
                String value = keyValuePair[1].trim();
                if (!value.isBlank()) {
                    entry.value.add(value);
                }
            }
        }
        this.prefix.clear();
    }

    private void write(String field, String value) {
        this.write(field, Collections.singletonList(value));
    }

    private void write(String field, List<String> value) {
        IniEntry entry = new IniEntry(value, new ArrayList<>(this.descriptionBuffer));
        this.descriptionBuffer.clear();
        IniSection section = this.getSection();
        section.values.put(field, entry);
    }

    private <T> void write(String field, T[] value, Function<T, String> toString) {
        this.write(field, Arrays.stream(value).map(toString).toList());
    }

    private <T> T readSingle(String field, IConfigFormat.ValueDecoder<T> parser) throws ConfigValueMissingException {
        IniSection section = this.getSection();
        IniEntry entry = section.values.get(field);
        if (entry == null) {
            throw new ConfigValueMissingException();
        }
        List<String> values = entry.value;
        if (values.isEmpty()) {
            throw new ConfigValueMissingException();
        }
        String first = values.getFirst();
        return parser.decode(first);
    }

    private <T> T[] readArray(String field, Function<Integer, T[]> array, IConfigFormat.ValueDecoder<T> parser) throws ConfigValueMissingException {
        IniSection section = this.getSection();
        IniEntry entry = section.values.get(field);
        if (entry == null) {
            throw new ConfigValueMissingException();
        }
        List<String> values = entry.value;
        List<T> mapped = new ArrayList<>();
        for (String value : values) {
            try {
                mapped.add(parser.decode(value));
            } catch (IllegalArgumentException e) {
                // ignore empty arrays
            }
        }
        T[] out = array.apply(mapped.size());
        for (int i = 0; i < mapped.size(); i++) {
            out[i] = mapped.get(i);
        }
        return out;
    }

    private IniSection getSection() {
        return this.sectionMap.computeIfAbsent(this.getSectionId(), key -> {
            IniSection section = new IniSection(key, new LinkedHashMap<>(), new ArrayList<>(this.descriptionBuffer));
            this.descriptionBuffer.clear();
            return section;
        });
    }

    private String getSectionId() {
        if (this.prefix.isEmpty())
            return "root";
        return String.join(".", this.prefix);
    }

    private record IniSection(String name, Map<String, IniEntry> values, List<String> description) {

        public boolean isEmpty() {
            return this.values.isEmpty();
        }

        public boolean hasDescription() {
            return !this.description.isEmpty();
        }
    }

    private record IniEntry(List<String> value, List<String> description) {

        public boolean hasDescription() {
            return !this.description.isEmpty();
        }
    }
}
