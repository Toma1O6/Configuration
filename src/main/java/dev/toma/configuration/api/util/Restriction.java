package dev.toma.configuration.api.util;

import com.google.common.base.Preconditions;

import java.util.regex.Pattern;

/**
 * This class allows you to control which strings can users
 * put into TextFields.
 *
 * @since 1.0.2
 * @author Toma
 */
public class Restriction {

    private final Pattern pattern;
    private final String[] userFeedback;
    boolean showPattern;

    /**
     * Construct {@link Restriction} instance from input parameters
     * You can call {@link Restriction#showPattern()} to enable pattern addition into type description
     *
     * @param pattern Valid string pattern
     * @param userFeedback Response shown in UI when user tries to add invalid character
     * @return new {@link Restriction} instance
     * @see Pattern
     */
    public static Restriction newRestriction(Pattern pattern, String... userFeedback) {
        Preconditions.checkNotNull(pattern, "Pattern cannot be null");
        return new Restriction(pattern, userFeedback);
    }

    Restriction(Pattern pattern, String... userFeedback) {
        this.pattern = pattern;
        this.userFeedback = userFeedback;
    }

    /**
     * Enables addition of pattern string into
     * type description
     *
     * @return {@code this} {@link Restriction}
     */
    public Restriction showPattern() {
        this.showPattern = true;
        return this;
    }

    /**
     * Validates input string
     *
     * @param input String to validate
     * @return whether {@code input} is valid
     */
    public boolean isStringValid(String input) {
        return pattern.matcher(input).matches();
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String[] getUserFeedback() {
        return userFeedback;
    }

    public boolean shouldAddPatternIntoDesc() {
        return showPattern;
    }
}
