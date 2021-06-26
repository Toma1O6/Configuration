package dev.toma.configuration.api.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import dev.toma.configuration.api.ConfigSortIndexes;
import dev.toma.configuration.api.ICollectible;
import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.TypeKey;
import dev.toma.configuration.internal.ConfigHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CollectionType<T extends IConfigType<?>> extends AbstractConfigType<List<T>> implements ICollectible<T> {

    private boolean lockedSize;
    private final Supplier<T> factory;

    public CollectionType(String name, List<T> entry, Supplier<T> objectFactory, String... desc) {
        super(TypeKey.COLLECTION, name, entry, desc);
        this.factory = objectFactory;
    }

    public CollectionType(String name, Supplier<T> objectFactory, String... desc) {
        this(name, new ArrayList<>(), objectFactory, desc);
    }

    public CollectionType<T> lockSize() {
        this.lockedSize = true;
        return this;
    }

    public void add(T t) {
        this.get().add(t);
    }

    public void remove(T t) {
        this.get().remove(t);
    }

    public void remove(int index) {
        this.get().remove(index);
    }

    public boolean hasLockedSize() {
        return lockedSize;
    }

    @Override
    public T[] collect() {
        return get().toArray((T[]) new Object[0]);
    }

    @Override
    public List<T> load(JsonElement element) throws JsonParseException {
        List<T> list = this.get();
        list.clear();
        if(!element.isJsonArray()) {
            throw new JsonParseException("Invalid config entry: " + ConfigHandler.GSON_OUT.toJson(element));
        }
        JsonArray array = element.getAsJsonArray();
        for (JsonElement el : array) {
            if(el.isJsonObject()) {
                T t = factory.get();
                t.loadData(el.getAsJsonObject());
                list.add(t);
            }
        }
        return list;
    }

    @Override
    public JsonElement save(boolean isUpdate) {
        JsonArray array = new JsonArray();
        for (T t : this.get()) {
            t.saveData(array, isUpdate);
        }
        return array;
    }

    public T createElement() {
        return factory.get();
    }
}
