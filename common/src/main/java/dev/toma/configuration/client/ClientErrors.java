package dev.toma.configuration.client;

import dev.toma.configuration.config.validate.NumberRange;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.regex.Pattern;

public final class ClientErrors {

    public static final MutableComponent CHAR_VALUE_EMPTY = Component.translatable("text.configuration.error.character_value_empty");

    private static final String KEY_NAN = "text.configuration.error.nan";
    private static final String KEY_NUM_BOUNDS = "text.configuration.error.num_bounds";
    private static final String KEY_MISMATCHED_PATTERN = "text.configuration.error.pattern_mismatch";

    public static MutableComponent notANumber(String value) {
        return Component.translatable(KEY_NAN, value);
    }

    public static MutableComponent outOfBounds(Number number, NumberRange<?> range) {
        return Component.translatable(KEY_NUM_BOUNDS, number, range.min(), range.max());
    }

    public static MutableComponent invalidText(String text, Pattern pattern) {
        return Component.translatable(KEY_MISMATCHED_PATTERN, text, pattern.pattern());
    }
}
