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
    @Configurable.Synchronized
    @Configurable.Range(min = 10, max = 30)
    @Configurable.Gui.Slider
    public int number = 15;

    @Configurable
    @Configurable.UpdateRestriction(UpdateRestrictions.GAME_RESTART)
    public long longNumber = 16644564564561651L;

    @Configurable
    @Configurable.Gui.NumberFormat("0.0#")
    public float floatNumber = 151.3123F;

    @Configurable
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

    @Configurable(localization = Configurable.LocalizationPath.FULL)
    public long[] longArray = {13, 56, 133};

    @Configurable(localization = Configurable.LocalizationPath.FULL)
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

        @Configurable(localization = Configurable.LocalizationPath.FULL)
        public int testInt = 13;

        @Configurable
        public int testInt2 = 15;

        @Configurable
        public AnotherNestedTest test = new AnotherNestedTest();
    }

    public static class AnotherNestedTest {

        @Configurable(localization = Configurable.LocalizationPath.FULL)
        @Configurable.Synchronized
        @Configurable.Comment(localize = true, value = "Nested boolean value")
        public boolean bool = true;
    }
}
