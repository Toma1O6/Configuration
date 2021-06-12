package dev.toma.configuration.api.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import dev.toma.configuration.api.ConfigSortIndexes;
import dev.toma.configuration.api.client.ComponentFactory;
import dev.toma.configuration.api.util.Restriction;
import dev.toma.configuration.internal.ConfigHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.PatternSyntaxException;

public class StringType extends AbstractConfigType<String> {

    private Restriction restriction;

    public StringType(String name, String value, String... desc) {
        this(name, value, null, desc);
    }

    public StringType(String name, String value, Restriction restriction, String... desc) throws PatternSyntaxException {
        super(name, value, desc);
        if(restriction != null) {
            this.restriction = restriction;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ComponentFactory getComponentFactory() {
        return ComponentFactory.STRING;
    }

    @Override
    public void set(String s) {
        if(restriction != null && restriction.isStringValid(s)) {
            if(restriction.isStringValid(s)) {
                super.set(s);
            }
        } else super.set(s);
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
        if(restriction != null && restriction.shouldAddPatternIntoDesc()) {
            comments.add("Allowed pattern: " + restriction.getPattern().pattern());
        }
        return comments.toArray(new String[0]);
    }

    public boolean hasRestriction() {
        return restriction != null;
    }

    public Restriction getRestriction() {
        return restriction;
    }

    @Override
    public int getSortIndex() {
        return ConfigSortIndexes.STRING;
    }
}
