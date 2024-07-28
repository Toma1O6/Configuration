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

public class StringArrayValue extends AbstractArrayValue<String> {

    private Pattern pattern;
    private String defaultElementValue = "";

    public StringArrayValue(ValueData<String[]> valueData) {
        super(valueData);
    }

    @Override
    public String createElementInstance() {
        return this.defaultElementValue != null ? this.defaultElementValue : "";
    }

    @SuppressWarnings("MagicConstant")
    @Override
    protected void readFieldData(Field field) {
        super.readFieldData(field);
        Configurable.StringPattern stringPattern = field.getAnnotation(Configurable.StringPattern.class);
        if (stringPattern != null) {
            String value = stringPattern.value();
            this.defaultElementValue = stringPattern.defaultValue();
            try {
                this.pattern = Pattern.compile(value, stringPattern.flags());
            } catch (IllegalArgumentException e) {
                Configuration.LOGGER.error(ConfigIO.MARKER, "Invalid @StringPattern value for {} field - {}", this.getId(), e);
            }
            if (this.pattern != null && !this.pattern.matcher(this.defaultElementValue).matches()) {
                throw new IllegalArgumentException(String.format("Invalid config default value '%s' for field '%s' - does not match required pattern \\%s\\", this.defaultElementValue, this.getId(), this.pattern.toString()));
            }
        }
    }

    @Override
    protected String[] validateValue(String[] in) {
        String[] array = super.validateValue(in);
        if (this.pattern != null) {
            for (int i = 0; i < in.length; i++) {
                String string = in[i];
                if (!this.pattern.matcher(string).matches()) {
                    ConfigUtils.logCorrectedMessage(this.getId() + "[" + i + "]", string, this.defaultElementValue);
                    in[i] = this.defaultElementValue;
                }
            }
        }
        return array;
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.writeStringArray(this.getId(), this.get(Mode.SAVED));
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.setValue(format.readStringArray(this.getId()));
    }

    public static final class Adapter extends TypeAdapter<String[]> {

        @Override
        public void encodeToBuffer(ConfigValue<String[]> value, FriendlyByteBuf buffer) {
            saveToBuffer(value.get(), buffer, FriendlyByteBuf::writeUtf);
        }

        @Override
        public String[] decodeFromBuffer(ConfigValue<String[]> value, FriendlyByteBuf buffer) {
            return readFromBuffer(buffer, String[]::new, FriendlyByteBuf::readUtf);
        }

        @Override
        public ConfigValue<String[]> serialize(TypeAttributes<String[]> attributes, Object instance, TypeSerializer serializer) throws IllegalAccessException {
            return new StringArrayValue(ValueData.of(attributes));
        }
    }
}
