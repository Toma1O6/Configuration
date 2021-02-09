package dev.toma.configuration.client.screen.component;

import dev.toma.configuration.api.type.AbstractConfigType;
import dev.toma.configuration.api.type.DoubleType;
import dev.toma.configuration.api.type.IntType;
import dev.toma.configuration.api.type.StringType;
import dev.toma.configuration.client.screen.ComponentScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TextFieldComponent<T extends AbstractConfigType<?>> extends ConfigComponent<T> {

    private final ComponentScreen parentScreen;
    protected String displayedText;
    int characterRenderOffset;

    public TextFieldComponent(ComponentScreen parentScreen, T t, int x, int y, int width, int height) {
        super(t, x, y, width, height);
        this.parentScreen = parentScreen;
        this.displayedText = t.get().toString();
    }

    public abstract boolean isValid(char character);

    public abstract boolean validate();

    public abstract void updateValue(String value);

    @Override
    public void drawComponent(FontRenderer font, int mouseX, int mouseY, float partialTicks, boolean hovered) {
        boolean selected = parentScreen.isSelected(this);
        int color = this.getBackgroundColor(selected);
        float r = ((color >> 16) & 255) / 255.0F;
        float g = ((color >> 8) & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        drawColorShape(x, y, x + width, y + height, r, g, b, 1.0F);
        drawColorShape(x + 1, y + 1, x + width - 1, y + height - 1, 0.0F, 0.0F, 0.0F, 1.0F);
        if(characterRenderOffset == 0) {
            font.drawStringWithShadow(displayedText, x + 5, y + 6, selected ? 0xFFFF00 : 0xFFFFFF);
            drawCursor(selected, displayedText, font);
        } else {
            String text = displayedText.substring(characterRenderOffset);
            font.drawStringWithShadow(text, x + 5, y + 6, selected ? 0xFFFF00 : 0xFFFFFF);
            drawCursor(selected, text, font);
        }
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == 259) {
            if(!displayedText.isEmpty()) {
                displayedText = displayedText.substring(0, displayedText.length() - 1);
                if(characterRenderOffset > 0)
                    --characterRenderOffset;
            }
        } else if(keyCode == 256) {
            onUnselect();
            parentScreen.selectedTextField = null;
        }
    }

    @Override
    public void charTyped(char character, int modifiers) {
        if(this.isValid(character)) {
            this.displayedText += character;
            if(characterRenderOffset > 0) {
                ++characterRenderOffset;
            } else {
                FontRenderer font = Minecraft.getInstance().fontRenderer;
                int textWidth = font.getStringWidth(displayedText);
                if(textWidth > (width - 13)) {
                    ++characterRenderOffset;
                }
            }
        }
    }

    public int getBackgroundColor(boolean selected) {
        return 0xFFFFFF;
    }

    public void onUnselect() {
        if(this.validate()) {
            this.updateValue(displayedText);
            this.parentScreen.sendUpdate();
        }
    }

    @Override
    public void onUpdate() {
        this.displayedText = configType.get().toString();
    }

    void drawCursor(boolean selected, String text, FontRenderer font) {
        if(selected && System.currentTimeMillis() % 1000L <= 500L) {
            int w = font.getStringWidth(text);
            drawColorShape(x + 5 + w, y + 5, x + 6 + w, y + 15, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public static class IntegerField extends TextFieldComponent<IntType> {

        private static final Pattern PATTERN = Pattern.compile("-[1-9]?|-[1-9][0-9]+|[0-9]|[1-9][0-9]+");

        public IntegerField(ComponentScreen screen, IntType type, int x, int y, int width, int height) {
            super(screen, type, x, y, width, height);
        }

        public boolean isValidNumber(String entry) {
            Matcher matcher = PATTERN.matcher(entry);
            if(matcher.matches()) {
                try {
                    int value = Integer.parseInt(entry);
                    return configType.isInRange(value);
                } catch (NumberFormatException ex) {
                    return false;
                }
            }
            return false;
        }

        @Override
        public boolean validate() {
            return isValidNumber(displayedText);
        }

        @Override
        public void updateValue(String value) {
            configType.set(Integer.parseInt(value));
        }

        @Override
        public boolean isValid(char character) {
            return PATTERN.matcher(displayedText + character).matches();
        }

        @Override
        public int getBackgroundColor(boolean selected) {
            return isValidNumber(displayedText) ? 0xFFFFFF : 0xCC0000;
        }
    }

    public static class DecimalField extends TextFieldComponent<DoubleType> {

        private static final Pattern PATTERN = Pattern.compile("(?:-)?|(?:-)?[0-9]+(?:.([0-9]*))?");

        public DecimalField(ComponentScreen screen, DoubleType type, int x, int y, int width, int height) {
            super(screen, type, x, y, width, height);
        }

        public boolean isValidNumber(String entry) {
            Matcher matcher = PATTERN.matcher(entry);
            if(matcher.matches()) {
                try {
                    double value = Double.parseDouble(entry);
                    return configType.isInRange(value);
                } catch (NumberFormatException ex) {
                    return false;
                }
            }
            return false;
        }

        @Override
        public void updateValue(String value) {
            configType.set(Double.parseDouble(value));
        }

        @Override
        public boolean validate() {
            return isValidNumber(displayedText);
        }

        @Override
        public boolean isValid(char character) {
            return PATTERN.matcher(displayedText + character).matches();
        }

        @Override
        public int getBackgroundColor(boolean selected) {
            return isValidNumber(displayedText) ? 0xFFFFFF : 0xCC0000;
        }
    }

    public static class StringField extends TextFieldComponent<StringType> {

        public StringField(ComponentScreen screen, StringType type, int x, int y, int width, int height) {
            super(screen, type, x, y, width, height);
        }

        @Override
        public boolean validate() {
            return !configType.hasPattern() || configType.getPattern().matcher(displayedText).matches();
        }

        @Override
        public boolean isValid(char character) {
            return true;
        }

        @Override
        public int getBackgroundColor(boolean selected) {
            return validate() ? 0xFFFFFF : 0xCC0000;
        }

        @Override
        public void updateValue(String value) {
            configType.set(displayedText);
        }
    }
}
