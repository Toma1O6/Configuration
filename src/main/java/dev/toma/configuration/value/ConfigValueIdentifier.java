package dev.toma.configuration.value;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

// Format modId:filename@group.field
public final class ConfigValueIdentifier {

    private final ResourceLocation fileIdentifier;
    @Nullable
    private final String path;
    private final String field;

    public ConfigValueIdentifier(ResourceLocation fileIdentifier, @Nullable String path, String field) {
        this.fileIdentifier = fileIdentifier;
        this.path = path;
        this.field = field;
    }

    public ConfigValueIdentifier(String namespace, String file, @Nullable String path, String field) {
        this(new ResourceLocation(namespace, file), path, field);
    }

    public static ConfigValueIdentifier fromString(String identifier) {
        String[] components = identifier.split("@");
        if (components.length != 2) {
            throw new IllegalArgumentException("Invalid config value identifier! Must be in format modId:filename@group.field");
        }
        ResourceLocation fileId = new ResourceLocation(components[0]);
        String[] groups = components[1].split("\\.");
        if (groups.length == 0) {
            throw new IllegalArgumentException("Invalid config value identifier! Must be in format modId:filename@group.field");
        } else if (groups.length == 1) {
            return new ConfigValueIdentifier(fileId, null, groups[0]);
        } else {
            String[] prefixComponents = new String[groups.length - 1];
            System.arraycopy(groups, 0, prefixComponents, 0, prefixComponents.length);
            String prefix = String.join(".", prefixComponents);
            String field = groups[groups.length - 1];
            return new ConfigValueIdentifier(fileId, prefix, field);
        }
    }

    public ResourceLocation getFileIdentifier() {
        return this.fileIdentifier;
    }

    @Nullable
    public String getPath() {
        return path;
    }

    public String getField() {
        return field;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigValueIdentifier that = (ConfigValueIdentifier) o;
        return fileIdentifier.equals(that.fileIdentifier) && Objects.equals(path, that.path) && field.equals(that.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileIdentifier, path, field);
    }

    public String getFullFieldIdentifier() {
        if (this.path == null) {
            return this.field;
        }
        return this.path + "." + this.field;
    }

    @Override
    public String toString() {
        return fileIdentifier.toString() + "@" + this.getFullFieldIdentifier();
    }

    public static final class Processor {

        private final ResourceLocation file;
        private final List<String> nestedGroups;

        private Processor(ResourceLocation file) {
            this.file = file;
            this.nestedGroups = new ArrayList<>();
        }

        public static Processor forFile(ResourceLocation file) {
            return new Processor(Objects.requireNonNull(file));
        }

        public ConfigValueIdentifier field(String fieldName) {
            return new ConfigValueIdentifier(this.file, this.getCurrentDirectory(), Objects.requireNonNull(fieldName));
        }

        public void stepIn(String directory) {
            this.nestedGroups.add(Objects.requireNonNull(directory));
        }

        public void stepOut() {
            if (!this.nestedGroups.isEmpty()) {
                this.nestedGroups.remove(this.nestedGroups.size() - 1);
            }
        }

        private String getCurrentDirectory() {
            if (nestedGroups.isEmpty()) {
                return null;
            }
            StringBuilder builder = new StringBuilder();
            Iterator<String> iterator = this.nestedGroups.iterator();
            while (iterator.hasNext()) {
                String group = iterator.next();
                builder.append(group);
                if (iterator.hasNext()) {
                    builder.append(".");
                }
            }
            return builder.toString();
        }
    }
}
