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

    public final String[] getComments() {
        return this.valueData.getTooltip();
    }

    public final void useDefaultValue() {
        this.set(this.valueData.getDefaultValue());
    }

    public abstract void serialize(IConfigFormat format);

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
