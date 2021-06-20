package dev.toma.configuration.api.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.TypeKey;

import java.util.Objects;

public abstract class AbstractConfigType<T> implements IConfigType<T> {

    private final String name;
    protected TypeKey typeKey;
    protected String[] desc;
    private T t;

    public AbstractConfigType(TypeKey typeKey, String entryName, T t, String... desc) {
        this.typeKey = typeKey;
        this.name = entryName;
        this.t = Objects.requireNonNull(t, "Null value is not allowed!");
        this.desc = desc;
    }

    @Override
    public void generateComments() {
        this.desc = this.createDescription(desc);
    }

    protected String[] createDescription(String... strings) {
        return strings;
    }

    @Override
    public T get() {
        return t;
    }

    @Override
    public void set(T t) {
        this.t = t;
    }

    @Override
    public final void loadData(JsonObject object) throws JsonParseException {
        if(object.has("value")) {
            this.set(this.load(object.get("value")));
        }
    }

    @Override
    public final void saveData(JsonElement element, boolean isUpdate) {
        JsonObject obj = new JsonObject();
        if(desc != null && desc.length > 0) {
            JsonArray array = new JsonArray();
            for (String str : desc) {
                array.add(str);
            }
            obj.add("comment", array);
        }
        obj.add("value", this.save(isUpdate));
        if(element.isJsonObject()) {
            element.getAsJsonObject().add(name, obj);
        } else if(element.isJsonArray()) {
            element.getAsJsonArray().add(obj);
        }
    }

    @Override
    public String getId() {
        return name;
    }

    @Override
    public String[] getComments() {
        return desc;
    }

    @Override
    public int getSortIndex() {
        return getType().getSortIndex();
    }

    @Override
    public TypeKey getType() {
        return typeKey;
    }
}
