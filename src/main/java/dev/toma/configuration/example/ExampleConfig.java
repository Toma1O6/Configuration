package dev.toma.configuration.example;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.api.*;
import dev.toma.configuration.api.type.*;
import dev.toma.configuration.api.util.Nameable;
import dev.toma.configuration.api.util.NumberDisplayType;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

/**
 * @author Toma
 */
@Config
public class ExampleConfig implements ConfigPlugin {

    public static BooleanType exampleBoolean;
    public static IntType exampleInteger;
    public static IntType exampleIntegerRanged;
    public static DoubleType exampleDecimal;
    public static DoubleType exampleDecimalRanged;
    public static DoubleType exampleDecimalFormatted;
    public static StringType exampleString;
    public static StringType exampleStringPattern;
    public static EnumType<ExampleEnum> exampleEnum;
    public static ExampleObject exampleObject;
    public static CollectionType<StringType> exampleCollection;
    public static ColorType rgb;
    public static ColorType argb;

    @Override
    public void buildConfigStructure(ConfigCreator builder) {
        exampleBoolean = builder.createBoolean("Boolean", false, "This is an example of boolean data type");
        exampleInteger = builder.createInt("Integer", 1234, "This is an example of integer data type");
        exampleIntegerRanged = builder.createInt("Ranged Integer", 10, 0, 20, "Example of integer with value range from 0 to 20").setDisplay(NumberDisplayType.TEXT_FIELD_SLIDER);
        exampleDecimal = builder.createDouble("Decimal", 12.43, "Example of decimal data type");
        exampleDecimalRanged = builder.createDouble("Ranged Decimal", 43.21, 27.5, 55.0, "Example of decimal with value range from 27.5 to 55.0").setDisplay(NumberDisplayType.SLIDER);
        exampleDecimalFormatted = builder.createDouble("Formatted Decimal", 43.123456, "Example of formatted decimal to two decimal spaces").setFormatting(new DecimalFormat("#.##"));
        exampleString = builder.createString("String", "This is string value", "Example of string data type");
        exampleStringPattern = builder.createString("Pattern String", "namespace:path", Pattern.compile("([a-z0-9]+[_.-]?)+:([a-z0-9]+[/._-]?)+"), "Example of resource location pattern");
        exampleEnum = builder.createEnum("Enum", ExampleEnum.ENTRY_2, "Example of enum data type");
        exampleObject = builder.createObject(new ExampleObject("Example Object", 12, "This is an object which contains multiple entries"));
        exampleCollection = builder.createFillList("List", () -> new StringType("", "null", Pattern.compile("[a-zA-z]*")), listType -> {
            listType.add(new StringType("", "value 1"));
            listType.add(new StringType("", "value 2"));
            listType.add(new StringType("", "value 3"));
            listType.add(new StringType("", "value 4"));
            listType.add(new StringType("", "value 5"));
            listType.add(new StringType("", "value 6"));
            listType.add(new StringType("", "value 7"));
            listType.add(new StringType("", "value 8"));
            listType.add(new StringType("", "value 9"));
            listType.add(new StringType("", "value 10"));
        }, "This is an example of element list");
        rgb = builder.createColorRGB("RGB", "#00FF00", "Color in RGB format");
        argb = builder.createColorARGB("ARGB", "#56FFFF00", "Color in ARGB format");
    }

    @Override
    public String getModID() {
        return Configuration.MODID;
    }

    public enum ExampleEnum implements Nameable {
        ENTRY_0,
        ENTRY_1,
        ENTRY_2;

        @Override
        public String getUnformattedName() {
            return this.name();
        }

        @Override
        public String getFormattedName() {
            return this.getUnformattedName();
        }
    }

    public static class ExampleObject extends ObjectType {

        public IntType containedInteger;

        private final int initialValue;

        public ExampleObject(String objectName, int integerValue, String... comments) {
            super(objectName, comments);
            initialValue = integerValue;
        }

        @Override
        public void buildStructure(ConfigCreator configCreator) {
            containedInteger = configCreator.createInt("Integer In Object", initialValue, 0, 45, "This is a ranged integer inside object");
        }
    }
}
