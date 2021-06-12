package dev.toma.configuration.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.toma.configuration.api.client.ComponentFactory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IConfigType<T> {

    String getId();

    String[] getComments();

    void generateComments();

    T get();

    void set(T t);

    T load(JsonElement element) throws JsonParseException;

    void loadData(JsonObject object) throws JsonParseException;

    void saveData(JsonElement element, boolean isUpdate);

    JsonElement save(boolean isUpdate);

    int getSortIndex();

    @OnlyIn(Dist.CLIENT)
    ComponentFactory getComponentFactory();
}
