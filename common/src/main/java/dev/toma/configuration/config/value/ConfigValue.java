package dev.toma.configuration.config.value;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.ConfigUtils;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.FieldVisibility;
import dev.toma.configuration.config.UpdateRestrictions;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.io.ConfigurationFileManager;
import dev.toma.configuration.config.util.IDescriptionProvider;
import dev.toma.configuration.config.util.NoteDescriptionProvider;
import dev.toma.configuration.config.util.ValueListener;
import dev.toma.configuration.config.validate.*;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.message.FormattedMessage;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class ConfigValue<T> implements IConfigValue<T> {

    protected final ValueData<T> valueData;
    private T pendingValue;
    private T activeValue;
    private T networkSavedValue;
    private boolean synchronizeToClient;
    private UpdateRestrictions updateRestriction = UpdateRestrictions.NONE;
    private final List<ValueFixer<T>> correctors = new ArrayList<>();
    private final List<Validator<T>> validators = new ArrayList<>();
    private final List<ValueListener<T>> listeners = new ArrayList<>();
    private ValidationResult validationResultHolder;
    private final List<IDescriptionProvider<T>> descriptionProviders = new ArrayList<>();
    private FieldVisibility fieldVisibility = FieldVisibility.NORMAL;

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

    @Override // thank you Forge for requiring this pointless implementation...
    public T get() {
        return IConfigValue.super.get();
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
        ConfigurationFileManager.ConfigEnvironment environment = ConfigurationFileManager.getEnvironment();
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
            this.pendingValue = this.processNewValue(value);
            this.notifyListeners(this.pendingValue);
            this.valueData.getContext().setValue(value);
        }
    }

    @Override
    public void revertChanges() {
        this.pendingValue = null;
        this.notifyListeners(this.activeValue);
        this.valueData.getContext().setValue(this.activeValue);
    }

    @Override
    public void revertChangesToDefault() {
        this.pendingValue = null;
        this.activeValue = this.valueData.getDefaultValue();
        this.notifyListeners(this.activeValue);
        this.valueData.getContext().setValue(this.activeValue);
    }

    public void clearNetworkValues() {
        this.networkSavedValue = null;
        this.notifyListeners(this.activeValue);
        this.valueData.setValueToMemory(this.activeValue);
    }

    @Override
    public final boolean isEditable() {
        ConfigurationFileManager.ConfigEnvironment environment = ConfigurationFileManager.getEnvironment();
        return this.updateRestriction.isEditableInEnvironment(environment);
    }

    public final void runGameInitEvents() {
        this.validators.forEach(validator -> validator.onGameLoaded(this));
        if (this instanceof IHierarchical hierarchical) {
            for (ConfigValue<?> value : hierarchical.children()) {
                value.runGameInitEvents();
            }
        }
        this.validateAndStoreResult(this.activeValue);
    }

    public final void forceSetValue(T value) {
        T corrected = this.processNewValue(value);
        this.pendingValue = null;
        this.activeValue = corrected;
        this.notifyListeners(this.activeValue);
        this.valueData.setValueToMemory(corrected);
    }

    public final void setFromNetwork(T value) {
        value = this.processNewValue(value);
        this.networkSavedValue = value;
        this.notifyListeners(this.networkSavedValue);
        this.valueData.setValueToMemory(value);
    }

    public final void forceSetDefaultValue() {
        this.forceSetValue(this.valueData.getDefaultValue());
    }

    public final T processNewValue(T in) {
        T fixedValue = in;
        for (ValueFixer<T> fixer : this.correctors) {
            fixedValue = fixer.fixValue(fixedValue);
        }
        if (fixedValue == null) {
            fixedValue = this.valueData.getDefaultValue();
            this.validationResultHolder = null;
        }
        this.validateAndStoreResult(in);
        if (this.validationResultHolder != null && !this.validationResultHolder.isValid()) {
            fixedValue = this.valueData.getDefaultValue();
        }
        return fixedValue;
    }

    public final void validateAndStoreResult(T value) {
        ValidationResult result = this.performAdditionalValidations(value);
        if (result.isWarningOrError()) {
            this.validationResultHolder = result;
        } else {
            this.validationResultHolder = null;
        }
    }

    @Override
    public final String getId() {
        return this.valueData.getId();
    }

    public final void setParent(ConfigValue<?> parent) {
        this.valueData.setParent(parent);
    }

    public final void processAnnotations(Field field) {
        this.synchronizeToClient = field.isAnnotationPresent(Configurable.Synchronized.class);
        Configurable.UpdateRestriction restriction = field.getAnnotation(Configurable.UpdateRestriction.class);
        if (restriction != null) {
            this.updateRestriction = restriction.value();
            if (this.updateRestriction == UpdateRestrictions.GAME_RESTART && this.shouldSynchronize()) {
                throw new IllegalArgumentException("Config value which can be updated only on game restart cannot be synchronized! Field " + field.getDeclaringClass().getCanonicalName() + "." + field.getName());
            }

            // Shows warning about game restart being needed before the value is actually applied
            if (this.updateRestriction == UpdateRestrictions.GAME_RESTART) {
                this.addValidator(new GameRestartValidator<>());
            }
        }
        // Network value processing
        if (this.shouldSynchronize()) {
            this.updateRestriction = UpdateRestrictions.MAIN_MENU;
            this.addDescriptionProvider(NoteDescriptionProvider.note(NoteDescriptionProvider.SYNCHRONIZED));
        } else if (this.updateRestriction.isRestricted()) {
            this.addDescriptionProvider(NoteDescriptionProvider.note(NoteDescriptionProvider.RESTRICTION.apply(this.updateRestriction)));
        }
        // Visibility annotation processing
        Configurable.Gui.Visibility visibility = field.getAnnotation(Configurable.Gui.Visibility.class);
        if (visibility != null && visibility.value() != FieldVisibility.NORMAL) {
            this.fieldVisibility = visibility.value();
            if (this.fieldVisibility == FieldVisibility.ADVANCED) {
                this.addDescriptionProvider(NoteDescriptionProvider.note(FieldVisibility.ADVANCED.getLabel()));
            }
        }
        // Additional type annotation processing by children implementations
        this.processAdditionalAnnotations(field);
        // Dependencies
        Configurable.DependsOn dependsOn = field.getAnnotation(Configurable.DependsOn.class);
        if (dependsOn != null) {
            Configurable.DependsOn.ActiveMod[] modRequirements = dependsOn.mods();
            for (Configurable.DependsOn.ActiveMod mod : modRequirements) {
                this.addValidator(new RequiredModValidator<>(mod));
            }
            Configurable.DependsOn.ConfigValue[] valueRequirements = dependsOn.configValues();
            for (Configurable.DependsOn.ConfigValue configValue : valueRequirements) {
                this.addValidator(new SpecificValueValidator<>(configValue));
            }
        }
        // Auto-registration of validators
        Configurable.Validate validate = field.getAnnotation(Configurable.Validate.class);
        if (validate != null) {
            Class<? extends Validator<?>>[] types = validate.value();
            for (Class<? extends Validator<?>> validatorType : types) {
                try {
                    this.autoRegisterValidator(validatorType);
                } catch (Exception e) {
                    Configuration.LOGGER.error(new FormattedMessage("Failed to register config value validator for field '{}', skipping", this.getId()), e);
                    if (Configuration.PLATFORM.isDevelopmentEnvironment()) {
                        throw new RuntimeException("Failed to register validator", e);
                    }
                }
            }
        }
    }

    protected boolean isChanged(T saved, T pending) {
        return this.isEditable() && !saved.equals(pending);
    }

    protected void processAdditionalAnnotations(Field field) {

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
        return Objects.toString(this.getActiveValue());
    }

    @Override
    public ValidationResult getValidationResult() {
        return this.validationResultHolder;
    }

    @Override
    public final void addDescriptionProvider(IDescriptionProvider<T> provider) {
        this.descriptionProviders.add(provider);
    }

    @Override
    public final void addValidator(Validator<T> validator) {
        this.validators.add(Objects.requireNonNull(validator));
    }

    @Override
    public final void addFixer(ValueFixer<T> corrector) {
        this.correctors.add(Objects.requireNonNull(corrector));
    }

    @Override
    public void addListener(ValueListener<T> listener) {
        this.listeners.add(Objects.requireNonNull(listener));
    }

    public void notifyListeners(T value) {
        this.listeners.forEach(listener -> listener.onValueChanged(this, value));
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

    public final FieldVisibility getFieldVisibility() {
        return this.fieldVisibility;
    }

    private ValidationResult performAdditionalValidations(T value) {
        List<ValidationResult> results = this.validators.stream()
                .map(validator -> validator.validate(value, this))
                .filter(ValidationResult::isWarningOrError)
                .toList();
        return ValidationHelper.aggregate(results);
    }

    @SuppressWarnings("unchecked")
    private void autoRegisterValidator(Class<? extends Validator<?>> type) throws Exception {
        try {
            Constructor<? extends Validator<?>> constructor = type.getConstructor();
            Validator<?> instance = constructor.newInstance();
            this.addValidator((Validator<T>) instance);
        } catch (NoSuchMethodException e) {
            Configuration.LOGGER.fatal(new FormattedMessage("No default constructor found for {}", type.getSimpleName()), e);
            throw e;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            Configuration.LOGGER.fatal(new FormattedMessage("Could not instantiate default constructor for {}", type.getSimpleName()), e);
            throw e;
        } catch (ClassCastException e) {
            Configuration.LOGGER.fatal(new FormattedMessage("Invalid validator data type {}", type.getSimpleName()), e);
            throw e;
        }
    }
}
