package dev.toma.configuration.io;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.Config;
import dev.toma.configuration.value.ConfigValue;
import dev.toma.configuration.value.ConfigValueIdentifier;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class ConfigClassReader {

    private static final Marker MARKER = MarkerManager.getMarker("ConfigClassReader");
    private final ConfigLoadingContext context;

    public ConfigClassReader(ConfigLoadingContext context) {
        this.context = context;
    }

    public <T> void loadValues() throws Exception {
        Field[] fields = this.context.getCfgClass().getDeclaredFields();
        for (Field field : fields) {
            Config.Entry entry = field.getAnnotation(Config.Entry.class);
            if (entry == null) {
                Configuration.LOGGER.debug(MARKER, "Skipping non-annotated field {}", field.getName());
                return;
            }
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers)) {
                Configuration.LOGGER.error(MARKER, "Skipping static config field {}, only instance type members are allowed", field.getName());
                return;
            }
            ITypeAdapter<T> adapter = ClassMatchers.getAdapter(field);
            if (adapter == null) {
                Configuration.LOGGER.error(MARKER, "Couldn't find appropriate type adapter for field {}", field.getName());
                return;
            }
            field.setAccessible(true);
            T value = adapter.fromField(field, this.context);
            if (!adapter.isContainer()) {
                ConfigValueIdentifier identifier = this.context.getProcessor().field(field.getName());
                ConfigValue<?> cfgValue = new ConfigValue<>(identifier, adapter, value);
                context.getMap().put(identifier, cfgValue);
            }
        }
    }
}
