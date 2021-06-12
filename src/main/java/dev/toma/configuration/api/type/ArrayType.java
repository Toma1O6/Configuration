package dev.toma.configuration.api.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import dev.toma.configuration.api.ConfigSortIndexes;
import dev.toma.configuration.api.ICollectible;
import dev.toma.configuration.api.INameable;
import dev.toma.configuration.api.client.ComponentFactory;
import dev.toma.configuration.internal.ConfigHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayType<T> extends AbstractConfigType<T> implements ICollectible<T> {

    final T[] values;
    final boolean isNameable;

    public ArrayType(String name, T value, T[] array, String... desc) {
        super(name, value, desc);
        this.values = array;
        isNameable = value instanceof INameable;
    }

    @OnlyIn(Dist.CLIENT)
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
            comments.add("# " + getElementKey(t));
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
            if(getElementKey(en).equalsIgnoreCase(name)) {
                return en;
            }
        }
        return values[0];
    }

    public String getElementKey(T t) {
        return isNameable ? ((INameable) t).getUnformattedName() : getDefaultElementString(t);
    }

    public String getElementDisplayName(T t) {
        return isNameable ? ((INameable) t).getFormattedName() : getDefaultElementString(t);
    }

    protected String getDefaultElementString(T t) {
        return t.toString();
    }

    @Override
    public JsonElement save(boolean isUpdate) {
        return new JsonPrimitive(getElementKey(get()));
    }

    @Override
    public T[] collect() {
        return values;
    }

    @Override
    public int getSortIndex() {
        return ConfigSortIndexes.ARRAY;
    }
}
