package dev.toma.configuration.config.value;

import dev.toma.configuration.config.ConfigUtils;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.UpdatePolicyType;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ConfigValue<T> implements Supplier<T>, Function<T, ConfigValue.ValidationResult> {

    protected final ValueData<T> valueData;
    private T value;
    private boolean synchronizeToClient;
    @Nullable
    private UpdatePolicyType updatePolicy;

    public ConfigValue(ValueData<T> valueData) {
        this.valueData = valueData;
        this.useDefaultValue();
    }

    @Override
    public final T get() {
        return value;
    }

    public final boolean shouldSynchronize() {
        return synchronizeToClient;
    }

    public final void set(T value) {
        T corrected = this.getCorrectedValue(value);
        if (corrected == null) {
            this.useDefaultValue();
            corrected = this.get();
        }
        this.value = corrected;
        this.valueData.setValueToMemory(corrected);
    }

    public final String getId() {
        return this.valueData.getId();
    }

    public final void setParent(@Nullable ConfigValue<?> parent) {
        this.valueData.setParent(parent);
    }

    public final void processFieldData(Field field) {
        this.synchronizeToClient = field.getAnnotation(Configurable.Synchronized.class) != null;
        Configurable.UpdatePolicy policy = field.getAnnotation(Configurable.UpdatePolicy.class);
        if (policy != null) {
            this.updatePolicy = policy.value();
        }
        this.readFieldData(field);
    }

    @Override
    public ValidationResult apply(T t) {
        return ValidationResult.valid();
    }

    protected void readFieldData(Field field) {

    }

    protected T getCorrectedValue(T in) {
        return in;
    }

    public final void useDefaultValue() {
        this.set(this.valueData.getDefaultValue());
    }

    protected abstract void serialize(IConfigFormat format);

    public final void serializeValue(IConfigFormat format) {
        format.addComments(valueData);
        this.serialize(format);
    }

    protected abstract void deserialize(IConfigFormat format) throws ConfigValueMissingException;

    public final void deserializeValue(IConfigFormat format) {
        try {
            this.deserialize(format);
        } catch (ConfigValueMissingException e) {
            this.useDefaultValue();
            ConfigUtils.logCorrectedMessage(this.getId(), null, this.get());
        }
    }

    public final TypeAdapter getAdapter() {
        return this.valueData.getContext().getAdapter();
    }

    public final String getFieldPath() {
        List<String> paths = new ArrayList<>();
        paths.add(this.getId());
        ConfigValue<?> parent = this;
        while ((parent = parent.valueData.getParent()) != null) {
            paths.add(parent.getId());
        }
        Collections.reverse(paths);
        return paths.stream().reduce("$", (a, b) -> a + "." + b);
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    public static final class ValidationResult {

        public static final String NUMBER_OUT_OF_RANGE = "configuration.config.validation.out_of_range";
        public static final String INVALID_STRING = "configuration.config.validation.invalid_string";

        private final boolean valid;
        private final ITextComponent message;

        private ValidationResult(boolean valid, String message, Object... data) {
            this.valid = valid;
            this.message = message != null ? new TranslationTextComponent(message, data) : null;
        }

        public static ValidationResult valid() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult error(String message, Object... data) {
            return new ValidationResult(false, message, data);
        }

        public boolean isValid() {
            return valid;
        }

        public ITextComponent getMessage() {
            return message;
        }
    }
}
