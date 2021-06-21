package dev.toma.configuration.api.client.widget;

import dev.toma.configuration.api.IConfigType;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;

public class InputWidget extends ConfigWidget<IConfigType<?>> {

    public boolean listening;
    public boolean isValid;
    public int cursorIndex;
    public int selectionIndex;
    public int cursorTick;
    public int selectionBackground = 0xFF666666;
    public int padding = 3;
    public int characterLimit = 64;
    private String text = "";

    protected InputWidget(WidgetType<? extends InputWidget> widgetType, IConfigType<?> type, int x, int y, int width, int height) {
        super(widgetType, type, x, y, width, height);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (!Screen.hasControlDown())
            listening = false;
        if (isMouseOver(mouseX, mouseY) && mouseButton == 0 && !visibilityState.isDisabled()) {
            listening = true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (visibilityState.isDisabled()) {
            return false;
        } else {
            if (Screen.isSelectAll(keyCode)) {

            } else if (Screen.isCopy(keyCode)) {

            } else if (Screen.isPaste(keyCode)) {

            } else if (Screen.isCut(keyCode)) {

            }
        }

        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void tick() {
        ++cursorTick;
    }

    public void insertText(String insertionEntry) {
        String s1 = text.substring(0, cursorIndex);
        String s2 = text.substring(cursorIndex);
        text = s1 + insertionEntry + s2;
        setCursorPosition(cursorIndex + insertionEntry.length());
        setSelectionIndex(cursorIndex);
    }

    public int getSelectionStart(FontRenderer renderer) {
        int i = Math.min(cursorIndex, selectionIndex);
        if (i == 0)
            return getX(padding);
        return getSubstringPosition(renderer, i);
    }

    public int getSelectionEnd(FontRenderer renderer) {
        int i = Math.max(cursorIndex, selectionIndex);
        if (i == 0)
            return getX(padding);
        if (i == text.length() - 1)
            return getX(padding + renderer.width(text));
        return getSubstringPosition(renderer, i);
    }

    public String getText() {
        return text;
    }

    private int getSubstringPosition(FontRenderer renderer, int endIndex) {
        String sub = text.substring(0, endIndex);
        int width = renderer.width(text);
        return getX(padding + width);
    }

    private void setCursorPosition(int pos) {
        cursorIndex = MathHelper.clamp(pos, 0, text.length());
    }

    private void setSelectionIndex(int index) {
        selectionIndex = MathHelper.clamp(index, 0, text.length());
    }
}
