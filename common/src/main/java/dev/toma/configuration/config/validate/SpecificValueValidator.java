package dev.toma.configuration.config.validate;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.ConfigValueLocation;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.IConfigValue;
import dev.toma.configuration.config.value.IConfigValueReadable;
import net.minecraft.network.chat.Component;

import java.util.*;

public class SpecificValueValidator<V> implements Validator<V> {

    private final ConfigValueLocation location;
    private final String[] accepts;
    private final boolean invert;

    public SpecificValueValidator(Configurable.DependsOn.ConfigValue value) {
        this(ConfigValueLocation.parse(value.location()), value.accepts(), value.invert());
    }

    public SpecificValueValidator(ConfigValueLocation location, String[] accepts, boolean invert) {
        this.location = location;
        this.accepts = accepts;
        this.invert = invert;
    }

    @Override
    public ValidationResult validate(V newValue, IConfigValueReadable<V> valueHolder) {
        ConfigHolder<?> holder = Configuration.getConfig(this.location.namespace()).orElse(null);
        if (holder == null) {
            return ValidationResult.warning(Component.translatable("text.configuration.validation.config_not_found", this.location));
        }
        Component configName = holder.getTitle();
        String path = this.location.path();
        IConfigValue<Object> configValue = holder.getConfigValue(path, Object.class).orElse(null);
        if (configValue == null) {
            return ValidationResult.warning(Component.translatable("text.configuration.validation.field_not_found", path, configName));
        }
        Object value = holder.getValue(path, Object.class).orElse(null);
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
        Configuration.getConfigValueHolder(this.location, Object.class)
                .ifPresent(value ->
                        value.addListener((cfgValue, updatedValue) -> configValue.validateAndStoreResult(configValue.getActiveValue())));
    }
}
