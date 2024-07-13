package dev.toma.configuration.config.value;

import java.util.function.Supplier;

public interface IConfigValue<T> extends Supplier<T> {

    // TODO ID, translation key, comment provider, parent value, field path
    // TODO methods to attach listeners, assign custom description provider, custom gui warnings

    @Override
    default T get() {
        return this.get(Mode.PENDING);
    }

    T get(Mode mode);

    void setValue(T value);

    void save();

    boolean isChanged();

    boolean isChangedFromDefault();

    boolean isEditable();

    enum Mode {
        SAVED,
        PENDING
    }
}
