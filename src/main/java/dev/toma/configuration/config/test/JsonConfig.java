package dev.toma.configuration.config.test;

import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;

@Config(id = "configuration:json", filename = "tests/json-test")
public final class JsonConfig {

    @Configurable
    @Configurable.Comment("Test boolean")
    @Configurable.Synchronized
    public final boolean testBoolean = true;

    @Configurable
    @Configurable.Comment("Test character")
    public final char testCharacter = 'o';

    @Configurable
    @Configurable.Comment("Test integer")
    public final int testInt = 16;

    @Configurable
    @Configurable.Comment("Test long")
    @Configurable.Range(max = 100L)
    public final long testLong = 1513213L;

    @Configurable
    @Configurable.Comment("Test float")
    @Configurable.DecimalRange(min = 0.0F, max = 1.0F)
    public final float testFloat = 156.31F;

    @Configurable
    @Configurable.Comment("Test double")
    @Configurable.DecimalRange(min = 133.0, max = 266.0)
    public final double testDouble = 1651.313;

    @Configurable
    @Configurable.Comment("Test string")
    public final String testString = "My test string";

    @Configurable
    @Configurable.Comment("Test boolean array")
    @Configurable.FixedSize
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
    @Configurable.Synchronized
    @Configurable.FixedSize
    public final double[] doubleArr = {846.13, 156, 3, 13124, -456.12};

    @Configurable
    @Configurable.Comment("Test string array")
    @Configurable.Synchronized
    public final String[] stringArr = {"First", "Second", "Third"};

    @Configurable
    @Configurable.Comment({"Testing enum", "Value should be selectable on GUI"})
    @Configurable.Synchronized
    public final TestEnum testEnum = TestEnum.VAL_4;

    @Configurable
    @Configurable.Comment({"Nested value", "Can be used as kind of category"})
    public final NestingTest nesting = new NestingTest(true);

    public static final class NestingTest {

        @Configurable
        @Configurable.Comment("This is nested private boolean")
        @Configurable.Synchronized
        private final boolean testNestings;

        @Configurable
        @Configurable.Comment("This is another nested value")
        @Configurable.Synchronized
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
        @Configurable.Synchronized
        @Configurable.Range(min = 10)
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
