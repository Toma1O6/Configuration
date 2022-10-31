package dev.toma.configuration.config.test;

import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.UpdatePolicyType;

@Config(id = "configuration:properties", filename = "tests/properties-test")
public final class PropertiesConfig {

    @Configurable
    @Configurable.Comment("Test boolean")
    public final boolean testBoolean = true;

    @Configurable
    @Configurable.Comment("Test character")
    public final char testCharacter = 'o';

    @Configurable
    @Configurable.Comment("Test integer")
    public final int testInt = 16;

    @Configurable
    @Configurable.Comment("Test long")
    public final long testLong = 1513213L;

    @Configurable
    @Configurable.Comment("Test float")
    public final float testFloat = 156.31F;

    @Configurable
    @Configurable.Comment("Test double")
    public final double testDouble = 1651.313;

    @Configurable
    @Configurable.Comment("Test string")
    public final String testString = "My test string";

    @Configurable
    @Configurable.Comment("Test boolean array")
    public final boolean[] boolArr = {true, false};

    @Configurable
    @Configurable.Comment("Test int array")
    public final int[] intArr = {12, 16, 71, 1};

    @Configurable
    @Configurable.Comment("Test long array")
    public final long[] longArr = {1566, 13213, 156613};

    @Configurable
    @Configurable.Comment("Test float array")
    public final float[] floatArr = {1513.153f, 13.156f};

    @Configurable
    @Configurable.Comment("Test double array")
    public final double[] doubleArr = {846.13, 156, 3, 13124, -456.12};

    @Configurable
    @Configurable.Comment("Test string array")
    @Configurable.UpdatePolicy(UpdatePolicyType.MENU)
    public final String[] stringArr = {"First", "Second", "Third"};

    @Configurable
    @Configurable.Comment({"Testing enum", "Value should be selectable on GUI"})
    @Configurable.UpdatePolicy(UpdatePolicyType.RESTART)
    public final TestEnum testEnum = TestEnum.VAL_4;

    @Configurable
    @Configurable.Comment({"Nested value", "Can be used as kind of category"})
    public final NestingTest nesting = new NestingTest(true);

    public static final class NestingTest {

        @Configurable
        @Configurable.Comment("This is nested private boolean")
        private final boolean testNestings;

        @Configurable
        @Configurable.Comment("This is another nested value")
        public final DoubleNestingTest random1 = new DoubleNestingTest(11);

        @Configurable
        @Configurable.Comment("And another one")
        public final DoubleNestingTest random2 = new DoubleNestingTest(156456);

        public NestingTest(boolean testNestings) {
            this.testNestings = testNestings;
        }
    }

    public static final class DoubleNestingTest {

        @Configurable
        @Configurable.Comment("This value is deeply nested")
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
