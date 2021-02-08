package dev.toma.configuration.api.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.toma.configuration.api.ConfigCreator;
import dev.toma.configuration.client.ComponentFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class ObjectType extends AbstractConfigType<Map<String, AbstractConfigType<?>>> {

    public ObjectType(String name, String... desc) {
        this(name, new HashMap<>(), desc);
    }

    public ObjectType(String name, Map<String, AbstractConfigType<?>> dataTree, String... desc) {
        super(name, dataTree, desc);
    }

    public abstract void buildStructure(ConfigCreator configCreator);

    @Override
    public ComponentFactory getDisplayFactory() {
        return ComponentFactory.OBJECT;
    }

    @Override
    public Map<String, AbstractConfigType<?>> load(JsonElement element) {
        if(element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            Map<String, AbstractConfigType<?>> data = this.get();
            for (Map.Entry<String, AbstractConfigType<?>> entry : data.entrySet()) {
                String key = entry.getKey();
                AbstractConfigType<?> type = entry.getValue();
                if(object.has(key)) {
                    type.loadData(object.getAsJsonObject(key));
                }
            }
            return data;
        }
        return null;
    }

    @Override
    public JsonElement save(boolean isUpdate) {
        JsonObject object = new JsonObject();
        Map<String, AbstractConfigType<?>> map = this.get();
        for (AbstractConfigType<?> type : map.values()) {
            if(!isUpdate)
                type.generateComments();
            type.saveData(object, isUpdate);
        }
        return object;
    }

    @Override
    public int getSortIndex() {
        return 7;
    }
}
