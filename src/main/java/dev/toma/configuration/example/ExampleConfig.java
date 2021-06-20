package dev.toma.configuration.example;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.api.*;
import dev.toma.configuration.api.type.*;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

/**
 * @author Toma
 */
@Config  //This is just example config, won't be loaded
public class ExampleConfig implements IConfigPlugin {

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
    public void buildConfig(IConfigWriter writer) {
        exampleBoolean = writer.writeBoolean("Boolean", false, "This is an example of boolean data type");
        exampleInteger = writer.writeInt("Integer", 1234, "This is an example of integer data type");
        exampleIntegerRanged = writer.writeBoundedInt("Ranged Integer", 10, 0, 20, "Example of integer with value range from 0 to 20").setDisplay(NumberDisplayType.TEXT_FIELD_SLIDER);
        exampleDecimal = writer.writeDouble("Decimal", 12.43, "Example of decimal data type");
        exampleDecimalRanged = writer.writeBoundedDouble("Ranged Decimal", 43.21, 27.5, 55.0, "Example of decimal with value range from 27.5 to 55.0").setDisplay(NumberDisplayType.SLIDER);
        exampleDecimalFormatted = writer.writeDouble("Formatted Decimal", 43.123456, "Example of formatted decimal to two decimal spaces").setFormatting(new DecimalFormat("#.##"));
        exampleString = writer.writeString("String", "This is string value", "Example of string data type");
        exampleStringPattern = writer.writeRestrictedString("Pattern String", "namespace:path", Restrictions.restrictStringByPattern(Pattern.compile("([a-z0-9]+[_.-]?)+:([a-z0-9]+[/._-]?)+"), true, "Non a-z_.- character is not allowed", "Must be separated by :"), "Example of resource location pattern");
        exampleEnum = writer.writeEnum("Enum", ExampleEnum.ENTRY_2, "Example of enum data type");
        exampleObject = writer.writeObject(spec -> new ExampleObject(spec, 12), "Example object", "Example of storing object / subcategory");
        exampleCollection = writer.writeApplyList("List", () -> new StringType("", "null", Restrictions.restrictStringByPattern(Pattern.compile("[a-zA-z\\s]*"), true, "Only A-Z character are allowed")), listType -> {
            listType.add(new StringType("", "a"));
            listType.add(new StringType("", "b"));
            listType.add(new StringType("", "c"));
            listType.add(new StringType("", "d"));
            listType.add(new StringType("", "e"));
            listType.add(new StringType("", "f"));
            listType.add(new StringType("", "g"));
            listType.add(new StringType("", "h"));
            listType.add(new StringType("", "i"));
            listType.add(new StringType("", "j"));
        }, "This is an example of element list");
        rgb = writer.writeColorRGB("RGB", "#00FF00", "Color in RGB format");
        argb = writer.writeColorARGB("ARGB", "#56FFFF00", "Color in ARGB format");
    }

    @Override
    public String getModID() {
        return Configuration.MODID;
    }

    public enum ExampleEnum implements INameable {
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

        public ExampleObject(IObjectSpec spec, int value) {
            super(spec);

            IConfigWriter writer = spec.getWriter();
            containedInteger = writer.writeInt("contained int", value, "This is integer inside another object");
        }
    }
}
