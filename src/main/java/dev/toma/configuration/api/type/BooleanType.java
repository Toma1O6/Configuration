package dev.toma.configuration.api.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import dev.toma.configuration.api.client.ComponentFactory;
import dev.toma.configuration.internal.ConfigHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BooleanType extends AbstractConfigType<Boolean> {

    public BooleanType(String name, boolean entry, String... desc) {
        super(name, entry, desc);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ComponentFactory getComponentFactory() {
        return ComponentFactory.BOOLEAN;
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
