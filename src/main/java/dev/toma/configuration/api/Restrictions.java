package dev.toma.configuration.api;

import java.util.List;
import java.util.regex.Pattern;

@SuppressWarnings("unchecked")
public final class Restrictions {

    private static final IRestriction<?> ALLOW = new AlwaysAllowed<>();

    public static <I> IRestriction<I> allow() {
        return (IRestriction<I>) ALLOW;
    }

    public static IRestriction<String> restrictStringByPattern(Pattern pattern, boolean addToDescription, String... feedback) {
        return new StringPattern(pattern, addToDescription, feedback);
    }

    public static IRestriction<String> colorRestriction(int colorComponents) {
        return new Color(colorComponents);
    }

    /* ========================================================= [ CLASSES ] ========================================================= */

    private static class StringPattern implements IRestriction<String> {
        protected final Pattern pattern;
        private final boolean addPatternDescription;
        private final String[] feedback;

        private StringPattern(Pattern pattern, boolean addToDescription, String... feedback) {
            this.pattern = pattern;
            this.addPatternDescription = addToDescription;
            this.feedback = feedback;
        }

        @Override
        public boolean isInputValid(String input) {
            return pattern.matcher(input).matches();
        }

        @Override
        public void addDescription(List<String> descriptionLines) {
            if (addPatternDescription) {
                descriptionLines.add("Allowed pattern: " + pattern.pattern());
            }
        }

        @Override
        public String[] getUserErrorMessage() {
            return feedback;
        }
    }

    private static class Color extends StringPattern {

        private Color(int components) {
            super(Pattern.compile("#([0-9a-fA-F]{" + (components * 2) + "})"), false, "Invalid color format");
        }
    }

    private static class AlwaysAllowed<I> implements IRestriction<I> {
        @Override
        public boolean isInputValid(I input) {
            return true;
        }

        @Override
        public void addDescription(List<String> descriptionLines) {
        }

        @Override
        public String[] getUserErrorMessage() {
            return new String[] {""};
        }
    }
}
