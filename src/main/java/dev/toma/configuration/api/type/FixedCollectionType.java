package dev.toma.configuration.api.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import dev.toma.configuration.api.client.ComponentFactory;
import dev.toma.configuration.api.util.Nameable;
import dev.toma.configuration.internal.Collectible;
import dev.toma.configuration.internal.ConfigHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FixedCollectionType<T extends Nameable> extends AbstractConfigType<T> implements Collectible<T> {

    final T[] values;

    public FixedCollectionType(String name, T value, T[] array, String... desc) {
        super(name, value, desc);
        this.values = array;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ComponentFactory getComponentFactory() {
        return ComponentFactory.ARRAY;
    }

    @Override
    protected String[] createDescription(String... strings) {
        List<String> comments = new ArrayList<>();
        comments.addAll(Arrays.asList(strings));
        comments.add("Allowed values:");
        for (T t : values) {
            comments.add("# " + t.getUnformattedName());
        }
        return comments.toArray(new String[0]);
    }

    @Override
    public T load(JsonElement element) throws JsonParseException {
        if(!element.isJsonPrimitive()) {
            throw new JsonParseException("Invalid config entry: " + ConfigHandler.GSON_OUT.toJson(element));
        }
        String name = element.getAsString();
        for (T en : values) {
            if(en.getUnformattedName().equalsIgnoreCase(name)) {
                return en;
            }
        }
        return values[0];
    }

    @Override
    public JsonElement save(boolean isUpdate) {
        return new JsonPrimitive(this.get().getUnformattedName());
    }

    @Override
    public T[] collect() {
        return values;
    }

    @Override
    public int getSortIndex() {
        return 5;
    }
}
