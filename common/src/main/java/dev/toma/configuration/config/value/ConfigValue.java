package dev.toma.configuration.config.value;

import dev.toma.configuration.client.IValidationHandler;
import dev.toma.configuration.config.ConfigUtils;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.UpdateRestrictions;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.io.ConfigIO;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ConfigValue<T> implements IConfigValue<T> {

    protected final ValueData<T> valueData;
    private T pendingValue;
    private T activeValue;
    private boolean synchronizeToClient;
    private UpdateRestrictions updateRestriction = UpdateRestrictions.NONE;
    private SetValueCallback<T> setValueCallback;

    public ConfigValue(ValueData<T> valueData) {
        this.valueData = valueData;
        this.forceSetValue(this.valueData.getDefaultValue());
    }

    @Override
    public T get(Mode mode) {
        if (this.pendingValue == null) {
            return this.activeValue;
        }
        return mode == Mode.SAVED ? this.activeValue : this.pendingValue;
    }

    @Override
    public boolean isChanged() {
        return this.pendingValue != null && this.isChanged(this.activeValue, this.pendingValue);
    }

    @Override
    public boolean isChangedFromDefault() {
        T t = this.get();
        return this.isChanged(t, this.valueData.getDefaultValue());
    }

    @Override
    public void save() {
        ConfigIO.ConfigEnvironment environment = ConfigIO.getEnvironment();
        if (this.pendingValue != null && this.updateRestriction.canApplyChangeInEnvironment(environment)) {
            this.forceSetValue(this.pendingValue);
            this.pendingValue = null;
        }
    }

    public boolean shouldSynchronize() {
        return synchronizeToClient;
    }

    @Override
    public final void setValue(T value) {
        this.pendingValue = value;
        this.valueData.getContext().setValue(value);
    }

    @Override
    public void revertChanges() {
        this.pendingValue = null;
        this.valueData.getContext().setValue(this.activeValue);
    }

    @Override
    public void revertChangesToDefault() {
        this.pendingValue = null;
        this.activeValue = this.valueData.getDefaultValue();
        this.valueData.getContext().setValue(this.activeValue);
    }

    @Override
    public final boolean isEditable() {
        ConfigIO.ConfigEnvironment environment = ConfigIO.getEnvironment();
        return this.updateRestriction.isEditableInEnvironment(environment);
    }

    public final void forceSetValue(T value) {
        T corrected = this.validateType(value);
        this.pendingValue = null;
        this.activeValue = corrected;
        this.valueData.setValueToMemory(corrected);
    }

    public final void forceSetDefaultValue() {
        this.forceSetValue(this.valueData.getDefaultValue());
    }

    public final T validateType(T in) {
        T corrected = this.validateValue(in);
        if (corrected == null) {
            corrected = this.valueData.getDefaultValue();
        }
        return corrected;
    }

    public final void setWithValidationHandler(T value, IValidationHandler handler) {
        this.invokeValueValidator(value, handler);
        this.setValue(value);
    }

    public final String getId() {
        return this.valueData.getId();
    }

    public final void setParent(ConfigValue<?> parent) {
        this.valueData.setParent(parent);
    }

    public final void processFieldData(Field field) {
        this.synchronizeToClient = field.isAnnotationPresent(Configurable.Synchronized.class);
        Configurable.UpdateRestriction restriction = field.getAnnotation(Configurable.UpdateRestriction.class);
        if (restriction != null) {
            this.updateRestriction = restriction.value();
            if (this.updateRestriction == UpdateRestrictions.GAME_RESTART && this.shouldSynchronize()) {
                throw new IllegalArgumentException("Config value which can be updated only on game restart cannot be synchronized! Field " + field.getDeclaringClass().getCanonicalName() + "." + field.getName());
            }
        }
        if (this.shouldSynchronize()) {
            this.updateRestriction = UpdateRestrictions.MAIN_MENU;
        }
        this.readFieldData(field);
    }

    protected boolean isChanged(T saved, T pending) {
        return !saved.equals(pending);
    }

    protected void readFieldData(Field field) {

    }

    protected T validateValue(T in) {
        return in;
    }

    public void setValueValidator(SetValueCallback<T> callback) {
        this.setValueCallback = callback;
    }

    public final void invokeValueValidator(T value, IValidationHandler handler) {
        if (this.setValueCallback != null) {
            this.setValueCallback.processValue(value, handler);
        }
    }

    protected abstract void serialize(IConfigFormat format);

    public final void serializeValue(IConfigFormat format) {
        format.addComments(valueData.getFileComments());
        this.serialize(format);
    }

    protected abstract void deserialize(IConfigFormat format) throws ConfigValueMissingException;

    public final void deserializeValue(IConfigFormat format) {
        try {
            this.deserialize(format);
        } catch (ConfigValueMissingException e) {
            this.forceSetValue(this.valueData.getDefaultValue());
            ConfigUtils.logCorrectedMessage(this.getId(), null, this.get());
        }
    }

    public final TypeAdapter.AdapterContext getSerializationContext() {
        return this.valueData.getContext();
    }

    @SuppressWarnings("unchecked")
    public final TypeAdapter<T> getAdapter() {
        return (TypeAdapter<T>) this.getSerializationContext().getAdapter();
    }

    public final Class<T> getValueType() {
        return this.valueData.getValueType();
    }

    public final ValueData<T> getValueData() {
        return this.valueData;
    }

    public final String getFullFieldPath() {
        return this.valueData.getFullFieldPath();
    }

    @Override
    public String toString() {
        return this.activeValue.toString();
    }

    @FunctionalInterface
    public interface SetValueCallback<V> {

        void processValue(V value, IValidationHandler handler);
    }
}
