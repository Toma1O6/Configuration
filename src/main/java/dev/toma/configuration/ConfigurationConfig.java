package dev.toma.configuration;

import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;

@Config(id = Configuration.MODID)
public final class ConfigurationConfig {

    @Configurable
    public final boolean testBoolean = true;

    @Configurable
    public final char testCharacter = 'o';

    @Configurable
    public final int testInt = 16;

    @Configurable
    public final int[] intArr = {12, 16, 71, 1};

    @Configurable
    public final TestEnum testEnum = TestEnum.VAL_4;

    @Configurable
    public final NestingTest nesting = new NestingTest(true);

    public static final class NestingTest {

        @Configurable
        public final boolean testNestings;

        @Configurable
        public final DoubleNestingTest random1 = new DoubleNestingTest(11);

        @Configurable
        public final DoubleNestingTest random2 = new DoubleNestingTest(156456);

        public NestingTest(boolean testNestings) {
            this.testNestings = testNestings;
        }
    }

    public static final class DoubleNestingTest {

        @Configurable
        public final int randomInt;

        public DoubleNestingTest(int randomInt) {
            this.randomInt = randomInt;
        }
    }

    public enum TestEnum {

        VAL_1,
        VAL_4
    }
}
