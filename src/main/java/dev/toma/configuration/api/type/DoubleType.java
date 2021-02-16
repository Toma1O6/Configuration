package dev.toma.configuration.api.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import dev.toma.configuration.api.client.ComponentFactory;
import dev.toma.configuration.api.util.NumberDisplayType;
import dev.toma.configuration.internal.ConfigHandler;
import dev.toma.configuration.internal.Formatting;
import dev.toma.configuration.internal.Ranged;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DoubleType extends AbstractConfigType<Double> implements Formatting<Double>, Ranged<Double> {

    private DecimalFormat format;
    private final double min, max;
    private NumberDisplayType displayType = NumberDisplayType.TEXT_FIELD;

    public DoubleType(String name, double entry, String... desc) {
        this(name, entry, -Double.MAX_VALUE, Double.MAX_VALUE, desc);
    }

    public DoubleType(String name, double entry, double min, double max, String... desc) {
        super(name, MathHelper.clamp(entry, min, max), desc);
        this.min = min;
        this.max = max;
    }

    public void setFromSlider(double pct) {
        double diff = this.getMax() - this.getMin();
        this.set(this.getMin() + diff * pct);
    }

    @Override
    public boolean isInRange(Double input) {
        return input >= min && input <= max;
    }

    public DoubleType setFormatting(DecimalFormat format) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        format.setDecimalFormatSymbols(symbols);
        this.format = format;
        return this;
    }

    public DoubleType setDisplay(NumberDisplayType type) {
        this.displayType = type;
        return this;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ComponentFactory getComponentFactory() {
        return ComponentFactory.DECIMAL;
    }

    @Override
    public void set(Double aDouble) {
        double d = MathHelper.clamp(aDouble, min, max);
        String f = formatNumber(d);
        super.set(Double.parseDouble(f));
    }

    @Override
    public Double load(JsonElement element) throws JsonParseException {
        if(!element.isJsonPrimitive()) {
            throw new JsonParseException("Invalid config entry: " + ConfigHandler.GSON_OUT.toJson(element));
        }
        return element.getAsDouble();
    }

    @Override
    public JsonElement save(boolean isUpdate) {
        return new JsonPrimitive(get());
    }

    @Override
    public String getFormatted() {
        return formatNumber(this.get());
    }

    @Override
    public String formatNumber(Double num) {
        if(format != null) {
            return format.format(num);
        }
        return num.toString();
    }

    @Override
    protected String[] createDescription(String... strings) {
        List<String> comments = new ArrayList<>();
        comments.addAll(Arrays.asList(strings));
        String left = "...";
        String right = "...";
        boolean rangeFlag = false;
        if(min > -Double.MAX_VALUE) {
            left = this.formatNumber(min);
            rangeFlag = true;
        }
        if(max < Double.MAX_VALUE) {
            right = this.formatNumber(max);
            rangeFlag = true;
        }
        if(rangeFlag) {
            comments.add(String.format("Range < %s ; %s >", left, right));
        }
        return comments.toArray(new String[0]);
    }

    @Override
    public Double getMin() {
        return min;
    }

    @Override
    public Double getMax() {
        return max;
    }

    public NumberDisplayType getDisplayType() {
        return displayType;
    }

    @Override
    public int getSortIndex() {
        return 2;
    }
}
