package dev.toma.configuration.api.client.component;

import dev.toma.configuration.api.client.screen.ComponentScreen;
import dev.toma.configuration.api.type.AbstractConfigType;
import dev.toma.configuration.api.type.DoubleType;
import dev.toma.configuration.api.type.IntType;
import dev.toma.configuration.api.type.StringType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TextFieldComponent<T extends AbstractConfigType<?>> extends ConfigComponent<T> {

    private final ComponentScreen parentScreen;
    protected String displayedText;
    int characterRenderOffset;
    String[] errorMessage = new String[0];
    int errWidth;
    boolean errorBlock;

    public TextFieldComponent(ComponentScreen parentScreen, T t, int x, int y, int width, int height) {
        super(t, x, y, width, height);
        this.parentScreen = parentScreen;
        this.displayedText = t.get().toString();
    }

    public TextFieldComponent<T> blockErrors() {
        this.errorBlock = true;
        return this;
    }

    public void setErrorMessage(@Nullable String... error) {
        this.errorMessage = error;
        this.errWidth = error != null ? this.getWidth(error) : 0;
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
        if(selected && errorMessage.length > 0 && !errorBlock) {
            int height = errorMessage.length * 10;
            int centerX = x + width / 2;
            int halfTextWidth = errWidth / 2;
            int textEnd = centerX + halfTextWidth;
            int posX = x - 11 - errWidth;
            int posY = y + (this.height - (height + 6)) / 2;
            drawColorShape(posX - 3, posY, posX + errWidth + 3, posY + height + 5, 0.75F, 0.2F, 0.2F, 0.7F);
            drawColorShape(posX - 2, posY + 1, posX + errWidth + 2, posY + height + 4, 0.0F, 0.0F, 0.0F, 0.7F);
            for (int i = 0; i < errorMessage.length; i++) {
                String message = errorMessage[i];
                font.drawStringWithShadow(message, posX, posY + 4 + i * 10, 0xAA4444);
            }
        }
    }

    @Override
    public void keyPressed(int keyCode) {
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
    public void charTyped(char character) {
        if(this.isValid(character)) {
            setErrorMessage();
            this.displayedText += character;
            if(characterRenderOffset > 0) {
                ++characterRenderOffset;
            } else {
                FontRenderer font = Minecraft.getMinecraft().fontRenderer;
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
        setErrorMessage();
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

    int getWidth(String[] input) {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        int width = 0;
        for (String s : input) {
            int i = font.getStringWidth(s);
            if(i > width)
                width = i;
        }
        return width;
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
                    if(configType.isInRange(value)) {
                        setErrorMessage();
                        return true;
                    } else {
                        setErrorMessage(value + " is outside allowed range!", "Allowed range is <" + configType.getMin() + ";" + configType.getMax() + ">");
                        return false;
                    }
                } catch (NumberFormatException ex) {
                    setErrorMessage(ex.getClass().getCanonicalName(), ex.getMessage());
                    return false;
                }
            } else setErrorMessage("Only numbers and '-' are allowed");
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

        private static final Pattern PATTERN = Pattern.compile("(?:-)?|(?:-)?[0-9]+(?:[.]([0-9]*))?");

        public DecimalField(ComponentScreen screen, DoubleType type, int x, int y, int width, int height) {
            super(screen, type, x, y, width, height);
        }

        public boolean isValidNumber(String entry) {
            Matcher matcher = PATTERN.matcher(entry);
            if(matcher.matches()) {
                try {
                    double value = Double.parseDouble(entry);
                    if(configType.isInRange(value)) {
                        setErrorMessage();
                        return true;
                    } else {
                        setErrorMessage(value + " is outside allowed range!", "Allowed range is <" + configType.getMin() + ";" + configType.getMax() + ">");
                        return false;
                    }
                } catch (NumberFormatException ex) {
                    setErrorMessage(ex.getClass().getCanonicalName(), ex.getMessage());
                    return false;
                }
            } else {
                setErrorMessage("Not a valid number");
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
            if(configType.hasRestriction()) {
                boolean valid = configType.getRestriction().isStringValid(displayedText);
                if(!valid) {
                    setErrorMessage(configType.getRestriction().getUserFeedback());
                } else setErrorMessage();
                return valid;
            }
            return true;
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
