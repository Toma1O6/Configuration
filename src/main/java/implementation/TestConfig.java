package implementation;

import dev.toma.configuration.api.Config;
import dev.toma.configuration.api.IConfigPlugin;
import dev.toma.configuration.api.IConfigWriter;
import dev.toma.configuration.api.type.IntType;

@Config
public class TestConfig implements IConfigPlugin {

    private IntType intType;

    @Override
    public String getModID() {
        return "test";
    }

    @Override
    public void buildConfig(IConfigWriter writer) {
        this.intType = writer.writeBoundedInt("test", 15, 0, 15);
    }

    public int getValue() {
        return intType.get();
    }
}
