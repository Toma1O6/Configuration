package implementation;

import dev.toma.configuration.Configuration;
import net.minecraftforge.fml.common.Mod;

@Mod("test")
public class TestMod {

    public TestMod() {
        Configuration.loadConfig(new TestConfig());
    }
}
