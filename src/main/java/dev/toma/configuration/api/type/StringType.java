package dev.toma.configuration.api.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import dev.toma.configuration.api.IRestriction;
import dev.toma.configuration.api.Restrictions;
import dev.toma.configuration.api.TypeKey;
import dev.toma.configuration.internal.ConfigHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringType extends AbstractConfigType<String> {

    private final IRestriction<String> restriction;

    public StringType(String name, String value, String... desc) {
        this(name, value, Restrictions.allow(), desc);
    }

    public StringType(String name, String value, IRestriction<String> restriction, String... desc) {
        this(TypeKey.STRING, name, value, restriction, desc);
    }

    public StringType(TypeKey typeKey, String name, String value, IRestriction<String> restriction, String... desc) {
        super(typeKey, name, value, desc);
        this.restriction = restriction != null ? restriction : Restrictions.allow();
    }

    @Override
    public void set(String s) {
        if(restriction.isInputValid(s)) {
            super.set(s);
        }
    }

    @Override
    public String load(JsonElement element) throws JsonParseException {
        if(!element.isJsonPrimitive()) {
            throw new JsonParseException("Invalid config entry: " + ConfigHandler.GSON_OUT.toJson(element));
        }
        return element.getAsString();
    }

    @Override
    public JsonElement save(boolean isUpdate) {
        return new JsonPrimitive(get());
    }

    @Override
    protected String[] createDescription(String... strings) {
        List<String> comments = new ArrayList<>();
        comments.addAll(Arrays.asList(strings));
        restriction.addDescription(comments);
        return comments.toArray(new String[0]);
    }

    public boolean hasRestriction() {
        return restriction != null;
    }

    public IRestriction<String> getRestriction() {
        return restriction;
    }
}
