package dev.toma.configuration.api.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import dev.toma.configuration.client.ComponentFactory;
import dev.toma.configuration.internal.ConfigHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class StringType extends AbstractConfigType<String> {

    private Pattern pattern;

    public StringType(String name, String value, String... desc) {
        this(name, value, null, desc);
    }

    public StringType(String name, String value, Pattern pattern, String... desc) throws PatternSyntaxException {
        super(name, value, desc);
        if(pattern != null) {
            this.pattern = pattern;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ComponentFactory getDisplayFactory() {
        return ComponentFactory.STRING;
    }

    @Override
    public void set(String s) {
        if(pattern != null) {
            Matcher matcher = pattern.matcher(s);
            if(matcher.matches()) {
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
        if(pattern != null && addPatternIntoDescription()) {
            comments.add("Allowed pattern: " + pattern.pattern());
        }
        return comments.toArray(new String[0]);
    }

    public boolean hasPattern() {
        return pattern != null;
    }

    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public int getSortIndex() {
        return 3;
    }

    public boolean addPatternIntoDescription() {
        return true;
    }
}
