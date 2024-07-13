package dev.toma.configuration.config.value;

import java.util.function.Supplier;

public interface IConfigValue<T> extends Supplier<T> {

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
