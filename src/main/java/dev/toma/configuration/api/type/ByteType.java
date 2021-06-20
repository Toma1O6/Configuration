package dev.toma.configuration.api.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import dev.toma.configuration.api.IBounded;
import dev.toma.configuration.api.NumberDisplayType;
import dev.toma.configuration.internal.ConfigHandler;

import java.util.Objects;

public class ByteType extends AbstractConfigType<Byte> implements IBounded<Byte> {

    private final byte min, max;
    private NumberDisplayType displayType = NumberDisplayType.TEXT_FIELD;

    public ByteType(String name, byte value, byte min, byte max, String... description) {
        super(null, name, value, description);
        this.min = min;
        this.max = max;
    }

    public ByteType setDisplay(NumberDisplayType type) {
        this.displayType = Objects.requireNonNull(type);
        return this;
    }

    @Override
    public Byte load(JsonElement element) throws JsonParseException {
        if(!element.isJsonPrimitive()) {
            throw new JsonParseException("Invalid config entry: " + ConfigHandler.GSON_OUT.toJson(element));
        }
        return element.getAsByte();
    }

    @Override
    public JsonElement save(boolean isUpdate) {
        return new JsonPrimitive(get());
    }

    @Override
    public boolean isWithinBounds(Byte input) {
        return input >= min && input <= max;
    }

    @Override
    public Byte getMin() {
        return min;
    }

    @Override
    public Byte getMax() {
        return max;
    }

    public NumberDisplayType getDisplayType() {
        return displayType;
    }
}
