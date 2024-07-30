package dev.toma.configuration.config.value;

import dev.toma.configuration.config.ConfigUtils;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.UpdateRestrictions;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.io.ConfigIO;
import dev.toma.configuration.config.util.IDescriptionProvider;
import dev.toma.configuration.config.util.NoteDescriptionProvider;
import dev.toma.configuration.config.validate.AggregatedValidationResult;
import dev.toma.configuration.config.validate.IConfigValueValidator;
import dev.toma.configuration.config.validate.IValidationResult;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.util.*;

public abstract class ConfigValue<T> implements IConfigValue<T> {

    public static final Component GAME_RESTART_REQUIRED = Component.translatable("text.configuration.validation.restart_required");

    protected final ValueData<T> valueData;
    private T pendingValue;
    private T activeValue;
    private T networkSavedValue;
    private boolean synchronizeToClient;
    private UpdateRestrictions updateRestriction = UpdateRestrictions.NONE;
    private final List<IConfigValueValidator<T>> validators = new ArrayList<>();
    private AggregatedValidationResult validationResultHolder;
    private final List<IDescriptionProvider<T>> descriptionProviders = new ArrayList<>();

    public ConfigValue(ValueData<T> valueData) {
        this.valueData = valueData;
        this.forceSetValue(this.valueData.getDefaultValue());
    }

    @Override
    public T get(Mode mode) {
        if (mode != Mode.SAVED && this.networkSavedValue != null) {
            return this.networkSavedValue;
        }
        if (this.pendingValue == null) {
            return this.activeValue;
        }
        return mode == Mode.SAVED && this.updateRestriction != UpdateRestrictions.GAME_RESTART ? this.activeValue : this.pendingValue;
    }

    public T getActiveValue() {
        return this.activeValue;
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

    @Override
    public Component getTitle() {
        return this.valueData.getTitle();
    }

    @Override
    public String[] getFileComments() {
        return this.valueData.getFileComments();
    }

    @Override
    public IConfigValue<?> parent() {
        return this.valueData.getParent();
    }

    @Override
    public Collection<String> getChildrenKeys() {
        return Collections.emptyList();
    }

    @Override
    public String getPath() {
        return this.valueData.getFullFieldPath();
    }

    public boolean shouldSynchronize() {
        return synchronizeToClient;
    }

    @Override
    public final void setValue(T value) {
        Objects.requireNonNull(value, "Config value cannot be null!");
        if (this.isEditable()) {
            this.pendingValue = this.validateType(value);
            this.valueData.getContext().setValue(value);
        }
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

    public void clearNetworkValues() {
        this.networkSavedValue = null;
        this.valueData.setValueToMemory(this.activeValue);
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

    public final void setFromNetwork(T value) {
        value = this.validateType(value);
        this.networkSavedValue = value;
        this.valueData.setValueToMemory(value);
    }

    public final void forceSetDefaultValue() {
        this.forceSetValue(this.valueData.getDefaultValue());
    }

    public final T validateType(T in) {
        T corrected = this.validateValue(in);
        if (corrected == null) {
            corrected = this.valueData.getDefaultValue();
            this.validationResultHolder = null;
        }
        AggregatedValidationResult validationResult = this.performAdditionalValidations(in);
        if (validationResult.severity() != IValidationResult.Severity.NONE) {
            this.validationResultHolder = validationResult;
            if (!validationResult.isValid()) {
                corrected = this.valueData.getDefaultValue();
            }
        } else {
            this.validationResultHolder = null;
        }
        return corrected;
    }

    @Override
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

            if (this.updateRestriction == UpdateRestrictions.GAME_RESTART) {
                this.validators.addFirst((t, wrapper) -> {
                    if (ConfigIO.getEnvironment() == ConfigIO.ConfigEnvironment.LOADING)
                        return IValidationResult.success();
                    if (this.isChanged(t, this.activeValue)) {
                        return IValidationResult.warning(GAME_RESTART_REQUIRED);
                    } else {
                        return IValidationResult.success();
                    }
                });
            }
        }
        if (this.shouldSynchronize()) {
            this.updateRestriction = UpdateRestrictions.MAIN_MENU;
            this.addDescriptionProvider(NoteDescriptionProvider.note(NoteDescriptionProvider.SYNCHRONIZED));
        } else if (this.updateRestriction.isRestricted()) {
            this.addDescriptionProvider(NoteDescriptionProvider.note(NoteDescriptionProvider.RESTRICTION.apply(this.updateRestriction)));
        }
        this.readFieldData(field);
    }

    protected boolean isChanged(T saved, T pending) {
        return this.isEditable() && !saved.equals(pending);
    }

    protected void readFieldData(Field field) {

    }

    protected T validateValue(T in) {
        return in;
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

    @Override
    public String toString() {
        return this.activeValue.toString();
    }

    @Override
    public AggregatedValidationResult getValidationResult() {
        return this.validationResultHolder;
    }

    @Override
    public final void addDescriptionProvider(IDescriptionProvider<T> provider) {
        this.descriptionProviders.add(provider);
    }

    @Override
    public final void addValidator(IConfigValueValidator<T> validator) {
        if (this instanceof ObjectValue) {
            throw new UnsupportedOperationException("Cannot register value validator for object config values");
        }
        this.validators.add(Objects.requireNonNull(validator));
    }

    @Override
    public final List<Component> getDescription() {
        List<Component> description = new ArrayList<>(this.valueData.getDescription());
        if (!this.descriptionProviders.isEmpty()) {
            List<Component> generated = new ArrayList<>();
            for (IDescriptionProvider<T> provider : this.descriptionProviders) {
                if (provider.replaceDefaultDescription())
                    description.clear();
                generated.addAll(provider.generate(this));
            }
            description.addAll(generated);
        }
        return description;
    }

    private AggregatedValidationResult performAdditionalValidations(T value) {
        List<IValidationResult> results = this.validators.stream()
                .map(validator -> validator.validate(value, this))
                .toList();
        return AggregatedValidationResult.aggregate(results);
    }
}
