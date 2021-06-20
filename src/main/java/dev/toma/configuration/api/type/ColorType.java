package dev.toma.configuration.api.type;

import dev.toma.configuration.api.IRestriction;
import dev.toma.configuration.api.TypeKey;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ColorType extends StringType {

    public ColorType(String name, String value, IRestriction<String> restriction, String... desc) {
        super(TypeKey.COLOR, name, value, restriction, desc);
    }

    public int getColor() {
        String colorString = "0x" + this.get().substring(1);
        long color = Long.decode(colorString);
        return (int) color;
    }
}
