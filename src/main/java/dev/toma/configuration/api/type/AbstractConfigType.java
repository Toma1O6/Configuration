package dev.toma.configuration.api.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.TypeKey;
import dev.toma.configuration.util.IListener;
import dev.toma.configuration.util.Listeners;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class AbstractConfigType<T> implements IConfigType<T> {

    private final String name;
    protected TypeKey typeKey;
    protected String[] desc;
    private T t;
    private final IListener<T> valueChanged = Listeners.multipleElementListener();

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
        T old = this.t;
        this.t = t;
        if (!old.equals(this.t)) {
            valueChanged.invoke(this.t);
        }
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

    @Override
    public void addListener(Consumer<T> listener) {
        valueChanged.listen(listener);
    }

    @Override
    public void removeListener(Consumer<T> listener) {
        valueChanged.stopListening(listener);
    }
}
