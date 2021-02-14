package dev.toma.configuration.api.type;

import dev.toma.configuration.api.client.ComponentFactory;
import dev.toma.configuration.api.util.Restriction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.regex.Pattern;

public class ColorType extends StringType {

    public ColorType(String name, String value, Pattern colorPattern, String... desc) {
        super(name, value, Restriction.newRestriction(colorPattern, "Invalid color format"), desc);
        if(!colorPattern.pattern().startsWith("#")) {
            throw new IllegalArgumentException("Color patterns must start with # character");
        }
    }

    public int getColor() {
        String colorString = "0x" + this.get().substring(1);
        long color = Long.decode(colorString);
        return (int) color;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ComponentFactory getComponentFactory() {
        return ComponentFactory.COLOR;
    }
}
