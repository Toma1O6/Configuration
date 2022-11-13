package dev.toma.configuration.config.value;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.ConfigUtils;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import dev.toma.configuration.config.io.ConfigIO;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

public class StringValue extends ConfigValue<String> {

    private Pattern pattern;
    private String descriptor;

    public StringValue(ValueData<String> valueData) {
        super(valueData);
    }

    @SuppressWarnings("MagicConstant")
    @Override
    protected void readFieldData(Field field) {
        Configurable.StringPattern stringPattern = field.getAnnotation(Configurable.StringPattern.class);
        if (stringPattern != null) {
            String value = stringPattern.value();
            this.descriptor = stringPattern.errorDescriptor().isEmpty() ? null : stringPattern.errorDescriptor();
            try {
                this.pattern = Pattern.compile(value, stringPattern.flags());
            } catch (IllegalArgumentException e) {
                Configuration.LOGGER.error(ConfigIO.MARKER, "Invalid @StringPattern value for {} field - {}", this.getId(), e);
            }
        }
    }

    @Override
    protected String getCorrectedValue(String in) {
        if (this.pattern != null) {
            if (!this.pattern.matcher(in).matches()) {
                String defaultValue = this.valueData.getDefaultValue();
                if (!this.pattern.matcher(defaultValue).matches()) {
                    throw new IllegalArgumentException(String.format("Invalid config default value '%s' for field '%s' - does not match required pattern \\%s\\", defaultValue, this.getId(), this.pattern.toString()));
                }
                ConfigUtils.logCorrectedMessage(this.getId(), in, defaultValue);
                return defaultValue;
            }
        }
        return in;
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.writeString(this.getId(), this.get());
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.set(format.readString(this.getId()));
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String getErrorDescriptor() {
        return descriptor;
    }

    public static final class Adapter extends TypeAdapter {

        @Override
        public void encodeToBuffer(ConfigValue<?> value, FriendlyByteBuf buffer) {
            buffer.writeUtf((String) value.get());
        }

        @Override
        public Object decodeFromBuffer(ConfigValue<?> value, FriendlyByteBuf buffer) {
            return buffer.readUtf();
        }

        @Override
        public ConfigValue<?> serialize(String name, String[] comments, Object value, TypeSerializer serializer, AdapterContext context) throws IllegalAccessException {
            return new StringValue(ValueData.of(name, (String) value, context, comments));
        }
    }
}
