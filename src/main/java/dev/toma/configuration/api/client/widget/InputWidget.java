package dev.toma.configuration.api.client.widget;

import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.IFormatted;
import dev.toma.configuration.api.IRestriction;
import dev.toma.configuration.api.client.screen.WidgetScreen;
import dev.toma.configuration.api.type.DoubleType;
import dev.toma.configuration.api.type.IntType;
import dev.toma.configuration.api.type.StringType;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class InputWidget<V, T extends IConfigType<V>> extends ConfigWidget<T> {

    public boolean listening;
    public boolean isValid;
    
    // STYLES START
    public int borderColor = 0xFFFFFFFF;
    public int invalidBorderColor = 0xFFAA0000;
    public int listeningBorderColor = 0xFFFFFF00;
    public int padding = 5;
    public int characterLimit = 64;
    public Function<String, String> formatter = Function.identity();
    public Predicate<Character> filter = character -> true;
    // STYLES END

    public int cursorIndex;
    public int cursorTick;
    public int characterOffset;
    public List<Component> errorMessage = Collections.emptyList();
    protected String text;
    private final Consumer<V> listenerRef;
    public WidgetScreen<?> parent;

    protected InputWidget(WidgetType<? extends InputWidget<?, ?>> widgetType, T type, int x, int y, int width, int height) {
        super(widgetType, type, x, y, width, height);
        background = 0xFF << 24;
        foreground = 0xFFFFFFFF;
        if (type instanceof IFormatted) {
            IFormatted formatted = (IFormatted) type;
            formatter = v -> formatted.format(type.get());
        }
        onValueChanged(type.get());
        checkValid();
        listenerRef = this::onValueChanged;
        getConfigType().addListener(listenerRef);
    }

    public abstract void setConfigValue(String boxValue);

    public void onValueChanged(V value) {
        text = formatter.apply(value.toString());
    }

    public void clearErrorMessage() {
        setErrorMessage((String[]) null);
    }

    public void setErrorMessage(String... message) {
        if (message == null)
            errorMessage = Collections.emptyList();
        else
            errorMessage = Arrays.stream(message).map(TextComponent::new).collect(Collectors.toList());
    }

    @Override
    public void save() {
        checkValid();
        if (isValid) {
            try {
                setConfigValue(formatter.apply(text));
            } catch (Exception exc) {
                isValid = false;
                setErrorMessage(exc.toString());
            }
        }
        getConfigType().removeListener(listenerRef);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (!Screen.hasControlDown()) {
            if (listening)
                save();
            listening = false;
        }
        if (isMouseOver(mouseX, mouseY) && mouseButton == 0 && !visibilityState.isDisabled()) {
            if (!listening) {
                getConfigType().addListener(listenerRef);
            }
            listening = true;
            setCursorPositionOnMouse(mouseX, mouseY);
            playPressSound();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!listening || visibilityState.isDisabled()) {
            return false;
        } else {
            Minecraft mc = Minecraft.getInstance();
            KeyboardHandler kbHandler = mc.keyboardHandler;
            if (Screen.isCopy(keyCode)) { // copy field content
                kbHandler.setClipboard(text);
            } else if (Screen.isPaste(keyCode)) { // paste clipboard content
                insertText(kbHandler.getClipboard());
            } else if (Screen.isCut(keyCode)) { // copy and remove field content
                kbHandler.setClipboard(text);
                text = "";
                cursorIndex = 0;
            } else {
                switch (keyCode) {
                    case GLFW.GLFW_KEY_BACKSPACE:
                    case GLFW.GLFW_KEY_DELETE:
                        deleteCharacter();
                        return false;
                    case GLFW.GLFW_KEY_ENTER:
                    case GLFW.GLFW_KEY_KP_ENTER:
                    case GLFW.GLFW_KEY_ESCAPE:
                        save();
                        listening = false;
                        return true;
                    case GLFW.GLFW_KEY_LEFT:
                        moveCursor(-1);
                        return false;
                    case GLFW.GLFW_KEY_RIGHT:
                        moveCursor(1);
                        return false;
                }
            }
        }
        checkValid();
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (!listening || visibilityState.isDisabled())
            return false;
        if (SharedConstants.isAllowedChatCharacter(codePoint)) {
            insertText(Character.toString(codePoint));
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        ++cursorTick;
    }

    @Override
    public void assignParent(WidgetScreen<?> screen) {
        this.parent = screen;
    }

    public void insertText(String insertionEntry) {
        String filtered = filterText(insertionEntry);
        int actualLength = text.length();
        int newLength = actualLength + filtered.length();
        String trimmedToLimit = newLength <= characterLimit ? filtered : filtered.substring(0, Math.min(filtered.length(), characterLimit - actualLength));
        if (cursorIndex == actualLength) {
            text += trimmedToLimit;
            setCursorPosition(text.length());
        } else {
            String s1 = text.substring(0, cursorIndex);
            String s2 = text.substring(cursorIndex);
            text = s1 + trimmedToLimit + s2;
            setCursorPosition(cursorIndex + trimmedToLimit.length());
        }
        checkValid();
    }

    public String filterText(String text) {
        StringBuilder builder = new StringBuilder();
        char[] characters = text.toCharArray();
        for (char character : characters) {
            if (SharedConstants.isAllowedChatCharacter(character) && filter.test(character)) {
                builder.append(character);
            }
        }
        return builder.toString();
    }

    public String getText() {
        return text;
    }

    public List<Component> getErrorMessage() {
        return errorMessage;
    }

    protected void checkValid() {
        isValid = true;
    }

    private int getSubstringPosition(Font renderer, int endIndex) {
        String sub = text.substring(0, endIndex);
        int width = renderer.width(text);
        return getX(padding + width);
    }

    private void setCursorPosition(int pos) {
        int textLength = text.length();
        cursorIndex = Mth.clamp(pos, 0, textLength);
        Font renderer = Minecraft.getInstance().font;
        int textFieldWidth = getWidth(-padding * 2);
        String oldTrimmedText = renderer.plainSubstrByWidth(text, textFieldWidth);
        int index = oldTrimmedText.length() + characterOffset;
        if (cursorIndex == characterOffset) {
            characterOffset -= renderer.plainSubstrByWidth(text, textFieldWidth, true).length();
        }
        if (cursorIndex > index) {
            characterOffset += cursorIndex - index;
        } else if (cursorIndex <= characterOffset) {
            characterOffset -= characterOffset - cursorIndex;
        }
        characterOffset = Mth.clamp(characterOffset, 0, textLength);
    }

    private void setCursorPositionOnMouse(double mouseX, double mouseY) {
        setCursorPosition(getCursorPositionFromMouse(mouseX, mouseY));
    }

    private int getCursorPositionFromMouse(double mouseX, double mouseY) {
        int i = Mth.floor(mouseX - getX()) - padding;
        Font renderer = Minecraft.getInstance().font;
        return renderer.plainSubstrByWidth(text, i).length();
    }

    private void moveCursor(int amount) {
        setCursorPosition(cursorIndex + amount);
    }

    private void deleteCharacter() {
        int pos = cursorIndex - characterOffset;
        if (pos > 0) {
            int min = cursorIndex - 1;
            int max = Math.min(cursorIndex, text.length());
            String s1 = text.substring(0, min);
            String s2 = text.substring(max);
            text = s1 + s2;
            --cursorIndex;
            checkValid();
        }
    }

    public static class StringInput extends InputWidget<String, StringType> {

        protected StringInput(WidgetType<? extends StringInput> widgetType, StringType type, int x, int y, int width, int height) {
            super(widgetType, type, x, y, width, height);

        }

        public static StringInput create(WidgetType<? extends StringInput> widgetType, IConfigType<?> type, int x, int y, int width, int height) {
            try {
                return new StringInput(widgetType, (StringType) type, x, y, width, height);
            } catch (ClassCastException cce) {
                throw new ReportedException(CrashReport.forThrowable(cce, "String text field is applicable only for string config types"));
            }
        }

        @Override
        public void setConfigValue(String boxValue) {
            getConfigType().set(boxValue);
        }

        @Override
        protected void checkValid() {
            IRestriction<String> restriction = getConfigType().getRestriction();
            isValid = restriction.isInputValid(text);
            if (isValid) {
                clearErrorMessage();
            } else {
                setErrorMessage(restriction.getUserErrorMessage());
            }
        }
    }

    public static class DoubleInput extends InputWidget<Double, DoubleType> {

        protected DoubleInput(WidgetType<? extends DoubleInput> widgetType, DoubleType type, int x, int y, int width, int height) {
            super(widgetType, type, x, y, width, height);
            filter = ch -> Character.isDigit(ch) || ch == '-' || ch == '.' || ch == 'E';
        }

        public static DoubleInput create(WidgetType<? extends DoubleInput> widgetType, IConfigType<?> type, int x, int y, int width, int height) {
            try {
                return new DoubleInput(widgetType, (DoubleType) type, x, y, width, height);
            } catch (ClassCastException cce) {
                throw new ReportedException(CrashReport.forThrowable(cce, "Double text field is applicable only for double config types"));
            }
        }

        @Override
        public void setConfigValue(String boxValue) {
            getConfigType().set(Double.parseDouble(boxValue));
        }

        @Override
        protected void checkValid() {
            try {
                double value = Double.parseDouble(getText());
                DoubleType type = getConfigType();
                if (!type.isWithinBounds(value)) {
                    isValid = false;
                    String min = formatter.apply(type.getMin().toString());
                    String max = formatter.apply(type.getMax().toString());
                    setErrorMessage("Value is out of bounds!", "Bounds: <" + min + ";" + max + ">");
                } else {
                    isValid = true;
                }
            } catch (NumberFormatException exc) {
                isValid = false;
                setErrorMessage("Invalid input", exc.getLocalizedMessage());
            }
        }
    }

    public static class IntegerInput extends InputWidget<Integer, IntType> {

        protected IntegerInput(WidgetType<? extends IntegerInput> widgetType, IntType type, int x, int y, int width, int height) {
            super(widgetType, type, x, y, width, height);
            filter = ch -> Character.isDigit(ch) || ch == '-';
        }

        public static IntegerInput create(WidgetType<? extends IntegerInput> widgetType, IConfigType<?> type, int x, int y, int width, int height) {
            try {
                return new IntegerInput(widgetType, (IntType) type, x, y, width, height);
            } catch (ClassCastException cce) {
                throw new ReportedException(CrashReport.forThrowable(cce, "Integer text field is applicable only for integer config types"));
            }
        }

        @Override
        public void setConfigValue(String boxValue) {
            getConfigType().set(Integer.parseInt(boxValue));
        }

        @Override
        protected void checkValid() {
            try {
                int value = Integer.parseInt(getText());
                IntType type = getConfigType();
                if (!type.isWithinBounds(value)) {
                    isValid = false;
                    String min = formatter.apply(type.getMin().toString());
                    String max = formatter.apply(type.getMax().toString());
                    setErrorMessage("Value is out of bounds!", "Bounds: <" + min + ";" + max + ">");
                } else {
                    isValid = true;
                }
            } catch (NumberFormatException exc) {
                isValid = false;
                setErrorMessage("Invalid input", exc.getLocalizedMessage());
            }
        }
    }
}
