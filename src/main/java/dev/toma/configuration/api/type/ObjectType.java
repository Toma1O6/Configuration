package dev.toma.configuration.api.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.toma.configuration.api.ConfigCreator;
import dev.toma.configuration.api.ConfigSortIndexes;
import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.client.ComponentFactory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

public abstract class ObjectType extends AbstractConfigType<Map<String, IConfigType<?>>> {

    public ObjectType(String name, String... desc) {
        this(name, new HashMap<>(), desc);
    }

    public ObjectType(String name, Map<String, IConfigType<?>> dataTree, String... desc) {
        super(name, dataTree, desc);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ComponentFactory getComponentFactory() {
        return ComponentFactory.OBJECT;
    }

    public abstract void buildStructure(ConfigCreator configCreator);

    @Override
    public Map<String, IConfigType<?>> load(JsonElement element) {
        if(element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            Map<String, IConfigType<?>> data = this.get();
            for (Map.Entry<String, IConfigType<?>> entry : data.entrySet()) {
                String key = entry.getKey();
                IConfigType<?> type = entry.getValue();
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
        Map<String, IConfigType<?>> map = this.get();
        for (IConfigType<?> type : map.values()) {
            if(!isUpdate)
                type.generateComments();
            type.saveData(object, isUpdate);
        }
        return object;
    }

    @Override
    public int getSortIndex() {
        return ConfigSortIndexes.OBJECT;
    }
}
