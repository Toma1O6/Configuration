package dev.toma.configuration.api.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.IObjectSpec;
import dev.toma.configuration.api.TypeKey;

import java.util.HashMap;
import java.util.Map;

public class ObjectType extends AbstractConfigType<Map<String, IConfigType<?>>> {

    public ObjectType(IObjectSpec spec) {
        super(TypeKey.OBJECT, spec.getObjectID(), new HashMap<>(), spec.getObjectDescription());
        spec.getWriter().setWritingObject(this);
    }

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
}
