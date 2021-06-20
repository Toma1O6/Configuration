package dev.toma.configuration.api.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import dev.toma.configuration.api.ConfigSortIndexes;
import dev.toma.configuration.api.TypeKey;
import dev.toma.configuration.internal.ConfigHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BooleanType extends AbstractConfigType<Boolean> {

    public BooleanType(String name, boolean entry, String... desc) {
        super(TypeKey.BOOLEAN, name, entry, desc);
    }

    @Override
    public JsonElement save(boolean isUpdate) {
        return new JsonPrimitive(get());
    }

    @Override
    public Boolean load(JsonElement element) throws JsonParseException {
        if(!element.isJsonPrimitive()) {
            throw new JsonParseException("Invalid config entry: " + ConfigHandler.GSON_OUT.toJson(element));
        }
        return element.getAsBoolean();
    }
}
