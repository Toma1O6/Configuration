package dev.toma.configuration;

import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.UpdateRestrictions;

import java.util.regex.Pattern;

@Config(id = "configuration-test", group = Configuration.MODID)
public final class TestingConfig {

    @Configurable
    public boolean bool = true;

    @Configurable
    public char character = 'c';

    @Configurable
    public byte byteSimple = 123;

    @Configurable
    @Configurable.Range(min = 10, max = 20)
    public byte byteRanged = 15;

    @Configurable
    @Configurable.Gui.Slider
    public byte byteSlider = 0;

    @Configurable
    public short shortSimple = 123;

    @Configurable
    @Configurable.Range(min = 10, max = 20)
    public short shortRanged = 15;

    @Configurable
    @Configurable.Gui.Slider
    public short shortSlider = 0;

    @Configurable
    public int intSimple = 15;

    @Configurable
    @Configurable.Range(min = 0, max = 255)
    @Configurable.Comment("Ranged value")
    public int intRanged = 10;

    @Configurable
    @Configurable.Range(min = 0)
    @Configurable.Gui.Slider
    public int intSlider = 15;

    @Configurable
    @Configurable.UpdateRestriction(UpdateRestrictions.GAME_RESTART)
    @Configurable.Comment(value = "Requires game restart", localize = true)
    @Configurable.Range(max = 50)
    public int intGameRestartRestriction = 99;

    @Configurable
    @Configurable.Synchronized
    @Configurable.Comment("Synchronized value")
    public int intSynchronized = 123;

    @Configurable
    @Configurable.UpdateRestriction(UpdateRestrictions.GAME_RESTART)
    public long longNumber = 16644564564561651L;

    @Configurable
    @Configurable.Gui.NumberFormat("0.0#")
    public float floatNumber = 151.3123F;

    @Configurable
    @Configurable.DecimalRange(max = 1024)
    public double doubleNumber = 316.15646556D;

    @Configurable
    @Configurable.StringPattern(value = "[a-z\\s]+", flags = Pattern.CASE_INSENSITIVE)
    public String string = "random text";

    @Configurable
    @Configurable.StringPattern(value = "#[0-9a-fA-F]{1,6}")
    @Configurable.Gui.ColorValue
    public String color = "#33AADD";

    @Configurable
    @Configurable.StringPattern(value = "#[0-9a-fA-F]{1,8}")
    @Configurable.Gui.ColorValue(isARGB = true)
    public String color2 = "#66771166";

    @Configurable
    @Configurable.FixedSize
    public boolean[] boolArray = {false, false, true, false};

    @Configurable
    @Configurable.Range(min = 50, max = 160)
    public int[] intArray = {153, 123, 54};

    @Configurable(key = Configurable.LocalizationKey.FULL)
    public long[] longArray = {13, 56, 133};

    @Configurable(key = Configurable.LocalizationKey.FULL)
    @Configurable.DecimalRange(min = 500.0F)
    public float[] floatArray = {135.32F, 1561.23F};

    @Configurable
    public String[] stringArray = {"minecraft:test"};

    @Configurable
    public TestEnum testEnum = TestEnum.C;

    @Configurable
    public TestEnum[] testEnumArray = { TestEnum.A, TestEnum.C };

    @Configurable
    public NestedTest nestedTest = new NestedTest();

    public enum TestEnum {
        A, B, C, D
    }

    public static class NestedTest {

        @Configurable(key = Configurable.LocalizationKey.FULL)
        public int testInt = 13;

        @Configurable
        public int testInt2 = 15;

        @Configurable
        public AnotherNestedTest test = new AnotherNestedTest();
    }

    public static class AnotherNestedTest {

        @Configurable(key = Configurable.LocalizationKey.FULL)
        @Configurable.Synchronized
        @Configurable.Comment(localize = true, value = "Nested boolean value")
        public boolean bool = true;
    }
}
