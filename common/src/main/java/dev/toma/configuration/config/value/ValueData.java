package dev.toma.configuration.config.value;

import com.google.common.base.Suppliers;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.adapter.TypeAdapter;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public final class ValueData<T> {

    private final TypeAdapter.TypeAttributes<T> attributes;
    private final Class<T> valueType;
    private ConfigValue<?> parent;

    private final Supplier<Component> title;
    private final Supplier<List<Component>> description;

    @SuppressWarnings("unchecked")
    private ValueData(TypeAdapter.TypeAttributes<T> attributes) {
        this.attributes = attributes;
        this.valueType = (Class<T>) attributes.value().getClass();

        this.title = Suppliers.memoize(() -> {
            String languageKey = this.getLanguageKey(attributes);
            return Component.translatable(languageKey);
        });
        this.description = Suppliers.memoize(() -> {
            int commentsLength = attributes.fileComments().length;
            String languageKey = this.getLanguageKey(attributes);
            return attributes.localizeComments()
                    ? this.generateDescription(languageKey, commentsLength)
                    : Arrays.stream(attributes.fileComments()).map(string -> (Component) Component.literal(string)).toList();
        });
    }

    public static <V> ValueData<V> of(TypeAdapter.TypeAttributes<V> attributes) {
        return new ValueData<>(attributes);
    }

    public String getId() {
        return this.attributes.id();
    }

    public String[] getFileComments() {
        return this.attributes.fileComments();
    }

    public Component getTitle() {
        return this.title.get();
    }

    public List<Component> getDescription() {
        return this.description.get();
    }

    public T getDefaultValue() {
        return this.attributes.value();
    }

    public void setValueToMemory(Object value) {
        this.attributes.context().setFieldValue(value);
    }

    public void setParent(ConfigValue<?> parent) {
        this.parent = parent;
    }

    public ConfigValue<?> getParent() {
        return this.parent;
    }

    public TypeAdapter.AdapterContext getContext() {
        return this.attributes.context();
    }

    public Class<T> getValueType() {
        return this.valueType;
    }

    public TypeAdapter.TypeAttributes<T> getAttributes() {
        return this.attributes;
    }

    public String getFullFieldPath() {
        List<String> paths = new ArrayList<>();
        paths.add(this.getId());
        ValueData<?> data = this;
        while (data.getParent() != null) {
            data = data.getParent().valueData;
            paths.add(data.getId());
        }
        Collections.reverse(paths);
        return paths.stream().reduce("", (a, b) -> a != null && !a.isBlank() ? (a + "." + b) : b);
    }

    public String getLanguageKey(TypeAdapter.TypeAttributes<T> attributes) {
        String owner = attributes.configOwner();
        String path = attributes.localization() == Configurable.LocalizationKey.FULL
                ? getFullFieldPath()
                : attributes.id();
        return String.format("config.%s.option.%s", owner, path);
    }

    private List<Component> generateDescription(String prefix, int count) {
        List<Component> comments = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Component localizedComment = Component.translatable(prefix + ".comment." + i);
            comments.add(localizedComment);
        }
        return Collections.unmodifiableList(comments);
    }
}
