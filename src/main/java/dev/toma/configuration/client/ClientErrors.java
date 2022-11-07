package dev.toma.configuration.client;

import dev.toma.configuration.config.value.DecimalValue;
import dev.toma.configuration.config.value.IntegerValue;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.regex.Pattern;

public final class ClientErrors {

    public static final ITextComponent CHAR_VALUE_EMPTY = new TranslationTextComponent("text.configuration.error.character_value_empty").withStyle(TextFormatting.ITALIC);

    private static final String KEY_NAN = "text.configuration.error.nan";
    private static final String KEY_NUM_BOUNDS = "text.configuration.error.num_bounds";
    private static final String KEY_MISMATCHED_PATTERN = "text.configuration.error.pattern_mismatch";

    public static ITextComponent notANumber(String value) {
        return new TranslationTextComponent(KEY_NAN, value);
    }

    public static ITextComponent outOfBounds(int i, IntegerValue.Range range) {
        return new TranslationTextComponent(KEY_NUM_BOUNDS, i, range.min(), range.max());
    }

    public static ITextComponent outOfBounds(long i, IntegerValue.Range range) {
        return new TranslationTextComponent(KEY_NUM_BOUNDS, i, range.min(), range.max());
    }

    public static ITextComponent outOfBounds(float i, DecimalValue.Range range) {
        return new TranslationTextComponent(KEY_NUM_BOUNDS, i, range.min(), range.max());
    }

    public static ITextComponent outOfBounds(double i, DecimalValue.Range range) {
        return new TranslationTextComponent(KEY_NUM_BOUNDS, i, range.min(), range.max());
    }

    public static ITextComponent invalidText(String text, Pattern pattern) {
        return new TranslationTextComponent(KEY_MISMATCHED_PATTERN, text, pattern);
    }
}
