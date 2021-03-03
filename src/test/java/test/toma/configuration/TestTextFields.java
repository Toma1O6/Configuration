package test.toma.configuration;

import org.junit.Test;
import org.lwjgl.glfw.GLFW;

import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class TestTextFields {

    @Test
    public void testIntegers() {
        IntegerField field = new IntegerField(10);
        assertEquals("10", field.text);
        clearCharacter(field);
        assertTrue(field.validate()); // 1
        assertTrue(field.setText("19"));
        assertTrue(field.setText("192"));
        field = new IntegerField(0);
        assertEquals("0", field.text);
        clearCharacter(field);
        assertFalse(field.validate());
        assertTrue(field.setText("-"));
        assertFalse(field.setText("-0"));
        assertTrue(field.setText("-1"));
        assertFalse(field.setText("1.0"));
        assertFalse(field.setText("1a"));
    }

    @Test
    public void testDecimals() {
        DoubleField field = new DoubleField(0.25);
        assertEquals("0.25", field.text);
        assertTrue(field.setText("0"));
        assertTrue(field.setText("0."));
        assertFalse(field.setText(".0"));
        assertTrue(field.setText("-"));
        assertTrue(field.setText("-0"));
        assertTrue(field.setText("-0."));
        assertTrue(field.setText("-0.0"));
        assertTrue(field.setText("1.2153"));
        assertFalse(field.setText("."));
        assertFalse(field.setText("1.0.0"));
    }

    @Test
    public void testStrings() {
        StringField field = new StringField("I am a string", null);
        assertEquals("I am a string", field.text);
        assertTrue(field.validate());
        assertTrue(field.setText(""));
        assertTrue(field.setText("abc123._-/[](){}"));
    }

    @Test
    public void testColors() {
        StringField rgb = new StringField("#FFFFFF", Pattern.compile("#[0-9a-fA-F]{1,6}"));
        assertTrue(rgb.validate());
        assertTrue(rgb.setText("#123456"));
        assertTrue(rgb.setText("#789abc"));
        assertTrue(rgb.setText("#ddeeff"));
        assertTrue(rgb.setText("#0"));
        assertFalse(rgb.setText("132"));
        assertFalse(rgb.setText("#"));
        assertFalse(rgb.setText("#1111111"));
    }

    @Test
    public void testIntegerPress() {
        IntegerField field = new IntegerField(123);
        clearCharacter(field);
        assertEquals("12", field.text);
        clearCharacter(field);
        assertEquals("1", field.text);
        clearCharacter(field);
        assertTrue(field.text.isEmpty());
        pressKey(field, '9', GLFW.GLFW_KEY_KP_9);
        assertTrue(field.validate());
        assertEquals("9", field.text);
        pressKey(field, 'a', GLFW.GLFW_KEY_A);
        assertEquals("9", field.text);
    }

    static void clearCharacter(TextField<?> field) {
        pressKey(field, '\b', GLFW.GLFW_KEY_BACKSPACE);
    }

    static void pressKey(TextField<?> field, char typedChar, int keycode) {
        if(!field.keyPressed(keycode)) {
            field.charTyped(typedChar);
        }
    }

    static abstract class TextField<T> {

        String text;

        public TextField(T t) {
            this.text = t.toString();
        }

        public abstract boolean isValid(char character);

        public abstract boolean validate();

        boolean setText(String text) {
            this.text = text;
            return validate();
        }

        public boolean keyPressed(int keycode) {
            if(keycode == GLFW.GLFW_KEY_BACKSPACE) {
                if(!text.isEmpty()) {
                    text = text.substring(0, text.length() - 1);
                }
                return true;
            }
            return false;
        }

        public void charTyped(char character) {
            if(isValid(character)) {
                text += character;
            }
        }
    }

    static class IntegerField extends TextField<Integer> {

        private static final Pattern PATTERN = Pattern.compile("-[1-9]?|-[1-9][0-9]+|[0-9]|[1-9][0-9]+");

        public IntegerField(int value) {
            super(value);
        }

        @Override
        public boolean isValid(char character) {
            return PATTERN.matcher(text + character).matches();
        }

        @Override
        public boolean validate() {
            return PATTERN.matcher(text).matches();
        }
    }

    static class DoubleField extends TextField<Double> {

        private static final Pattern PATTERN = Pattern.compile("(?:-)?|(?:-)?[0-9]+(?:[.]([0-9]*))?");

        DoubleField(double val) {
            super(val);
        }

        @Override
        public boolean isValid(char character) {
            return PATTERN.matcher(text + character).matches();
        }

        @Override
        public boolean validate() {
            return PATTERN.matcher(text).matches();
        }
    }

    static class StringField extends TextField<String> {

        final Pattern pattern;

        public StringField(String value, Pattern pattern) {
            super(value);
            this.pattern = pattern;
        }

        @Override
        public boolean isValid(char character) {
            return true;
        }

        @Override
        public boolean validate() {
            return pattern == null || pattern.matcher(text).matches();
        }
    }
}
