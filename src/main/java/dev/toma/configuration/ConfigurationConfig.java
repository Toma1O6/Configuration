package dev.toma.configuration;

import dev.toma.configuration.config.Config;

@Config(id = Configuration.MODID)
public final class ConfigurationConfig {

    @Config.Value
    public boolean testBoolean = true;

    @Config.Value
    public final int testInt = 16;

    @Config.Value
    public final NestingTest nesting = new NestingTest(true);

    public static final class NestingTest {

        @Config.Value
        public final boolean testNestings;

        @Config.Value
        public final DoubleNestingTest random1 = new DoubleNestingTest(11);

        @Config.Value
        public final DoubleNestingTest random2 = new DoubleNestingTest(156456);

        public NestingTest(boolean testNestings) {
            this.testNestings = testNestings;
        }
    }

    public static final class DoubleNestingTest {

        @Config.Value
        public final int randomInt;

        public DoubleNestingTest(int randomInt) {
            this.randomInt = randomInt;
        }
    }
}
