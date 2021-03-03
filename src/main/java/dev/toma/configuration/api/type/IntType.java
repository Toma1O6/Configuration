package dev.toma.configuration.api.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import dev.toma.configuration.api.client.ComponentFactory;
import dev.toma.configuration.api.util.NumberDisplayType;
import dev.toma.configuration.internal.ConfigHandler;
import dev.toma.configuration.internal.Ranged;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IntType extends AbstractConfigType<Integer> implements Ranged<Integer> {

    private final int min, max;
    private NumberDisplayType displayType = NumberDisplayType.TEXT_FIELD;

    public IntType(String name, int entry, String... desc) {
        this(name, entry, Integer.MIN_VALUE, Integer.MAX_VALUE, desc);
    }

    public IntType(String name, int entry, int min, int max, String... desc) {
        super(name, MathHelper.clamp(entry, min, max), desc);
        this.min = min;
        this.max = max;
    }

    public void setFromSlider(double pct) {
        int diff = this.getMax() - this.getMin();
        set(this.getMin() + (int) (diff * pct));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ComponentFactory getComponentFactory() {
        return ComponentFactory.INTEGER;
    }

    @Override
    public boolean isInRange(Integer input) {
        return input >= min && input <= max;
    }

    @Override
    protected String[] createDescription(String... strings) {
        List<String> comments = new ArrayList<>();
        comments.addAll(Arrays.asList(strings));
        String left = "...";
        String right = "...";
        boolean rangeFlag = false;
        if(min > Integer.MIN_VALUE) {
            left = min + "";
            rangeFlag = true;
        }
        if(max < Integer.MAX_VALUE) {
            right = max + "";
            rangeFlag = true;
        }
        if(rangeFlag) {
            comments.add(String.format("Range < %s ; %s >", left, right));
        }
        return comments.toArray(new String[0]);
    }

    @Override
    public Integer load(JsonElement element) throws JsonParseException {
        if(!element.isJsonPrimitive()) {
            throw new JsonParseException("Invalid config entry: " + ConfigHandler.GSON_OUT.toJson(element));
        }
        return element.getAsInt();
    }

    @Override
    public JsonElement save(boolean isUpdate) {
        return new JsonPrimitive(get());
    }

    @Override
    public void set(Integer integer) {
        super.set(MathHelper.clamp(integer, min, max));
    }

    public IntType setDisplay(NumberDisplayType type) {
        this.displayType = type;
        return this;
    }

    public NumberDisplayType getDisplayType() {
        return displayType;
    }

    @Override
    public Integer getMin() {
        return min;
    }

    @Override
    public Integer getMax() {
        return max;
    }

    @Override
    public int getSortIndex() {
        return 1;
    }
}
