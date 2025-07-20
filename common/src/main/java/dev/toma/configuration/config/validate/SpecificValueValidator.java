package dev.toma.configuration.config.validate;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.IConfigValue;
import dev.toma.configuration.config.value.IConfigValueReadable;
import net.minecraft.network.chat.Component;

import java.util.*;

public class SpecificValueValidator<V> implements Validator<V> {

    private final String config;
    private final String path;
    private final String[] accepts;
    private final boolean invert;

    public SpecificValueValidator(Configurable.DependsOn.ConfigValue value) {
        this(value.config(), value.path(), value.accepts(), value.invert());
    }

    public SpecificValueValidator(String config, String path, String[] accepts, boolean invert) {
        this.config = config;
        this.path = path;
        this.accepts = accepts;
        this.invert = invert;
    }

    @Override
    public ValidationResult validate(V newValue, IConfigValueReadable<V> valueHolder) {
        ConfigHolder<?> holder = Configuration.getConfig(this.config).orElse(null);
        if (holder == null) {
            return ValidationResult.warning(Component.translatable("text.configuration.validation.config_not_found", this.config));
        }
        Component configName = holder.getTitle();
        IConfigValue<Object> configValue = holder.getConfigValue(this.path, Object.class).orElse(null);
        if (configValue == null) {
            return ValidationResult.warning(Component.translatable("text.configuration.validation.field_not_found", this.path, configName));
        }
        Object value = holder.getValue(this.path, Object.class).orElse(null);
        if (value == null) {
            return ValidationResult.warning(Component.translatable("text.configuration.validation.value_not_found", configValue.getTitle(), configName));
        }
        String asString = value.getClass().isArray() ? Arrays.toString((Object[]) value) : Objects.toString(value);
        for (String required : this.accepts) {
            if (required.equalsIgnoreCase(asString) && !this.invert) {
                return ValidationResult.success();
            }
        }
        List<Component> warningLabel = new ArrayList<>();
        Component fieldName = configValue.getTitle();
        warningLabel.add(Component.translatable("text.configuration.validation.value_required", fieldName, configName));
        for (String acceptedValue : this.accepts) {
            warningLabel.add(Component.literal("- " + acceptedValue));
        }
        return ValidationResult.warning(warningLabel);
    }

    @Override
    public void onGameLoaded(IConfigValue<V> v) {
        ConfigValue<V> configValue = (ConfigValue<V>) v;
        Configuration.getConfig(this.config)
                .flatMap(holder -> holder.getConfigValue(this.path, Object.class))
                .ifPresent(value ->
                        value.addListener((cfgValue, updatedValue) -> configValue.validateAndStoreResult(configValue.getActiveValue())));
    }
}
