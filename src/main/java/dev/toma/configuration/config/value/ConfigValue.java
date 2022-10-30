package dev.toma.configuration.config.value;

import dev.toma.configuration.config.ConfigUtils;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.exception.ConfigValueMissingException;

import java.util.function.Supplier;

public abstract class ConfigValue<T> implements Supplier<T> {

    private final ValueData<T> valueData;
    private T value;

    public ConfigValue(ValueData<T> valueData) {
        this.valueData = valueData;
        this.useDefaultValue();
    }

    @Override
    public final T get() {
        return value;
    }

    public final void set(T value) {
        this.value = value; // TODO validation impl
        this.valueData.setValueToMemory(value);
    }

    public final String getId() {
        return this.valueData.getId();
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
}
