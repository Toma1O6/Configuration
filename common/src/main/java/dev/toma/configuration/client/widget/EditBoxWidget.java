package dev.toma.configuration.client.widget;

import dev.toma.configuration.client.theme.ConfigTheme;
import net.minecraft.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

// Shamelessly copied from Vanilla and adjusted as needed
// TODO implement it in a way we can reuse vanilla editBox instead
public class EditBoxWidget extends AbstractThemeWidget {

    public static final WidgetSprites SPRITES = new WidgetSprites(Identifier.withDefaultNamespace("widget/text_field"), Identifier.withDefaultNamespace("widget/text_field_highlighted"));
    private static final int CURSOR_INSERT_COLOR = 0xffd0d0d0;
    private static final String CURSOR_APPEND_CHARACTER = "_";
    private static final int CURSOR_BLINK_INTERVAL_MS = 300;
    private final Font font;
    private String value;
    private int maxLength;
    private boolean bordered;
    private boolean canLoseFocus;
    private int displayPos;
    private int cursorPos;
    private int highlightPos;
    private String suggestion;
    private Consumer<String> responder;
    private Predicate<String> filter;
    private BiFunction<String, Integer, FormattedCharSequence> formatter;
    private Component hint;
    private long focusedTime;
    private NumberFormatter numberFormatter;

    public EditBoxWidget(int x, int y, int width, int height, ConfigTheme theme, Font font) {
        super(x, y, width, height, theme);
        this.font = font;
        this.value = "";
        this.maxLength = 32;
        this.bordered = true;
        this.canLoseFocus = true;
        this.filter = Objects::nonNull;
        this.formatter = (text, i) -> FormattedCharSequence.forward(text, Style.EMPTY);
        this.focusedTime = Util.getMillis();
    }

    public void setFormatter(DecimalFormat formatter, Supplier<Number> provider) {
        this.numberFormatter = formatter != null ? new NumberFormatter(formatter, provider) : null;
    }

    public void setResponder(Consumer<String> $$0) {
        this.responder = $$0;
    }

    public void setFormatter(BiFunction<String, Integer, FormattedCharSequence> $$0) {
        this.formatter = $$0;
    }

    @Override
    protected MutableComponent createNarrationMessage() {
        Component $$0 = this.getMessage();
        return Component.translatable("gui.narrate.editBox", $$0, this.value);
    }

    public void setValue(String value) {
        if (this.filter.test(value)) {
            if (value.length() > this.maxLength) {
                this.value = value.substring(0, this.maxLength);
            } else {
                this.value = value;
            }

            this.moveCursorToEnd(false);
            this.setHighlightPos(this.cursorPos);
            this.onValueChange(value);
        }
    }

    public String getValue() {
        return this.value;
    }

    public String getHighlighted() {
        int $$0 = Math.min(this.cursorPos, this.highlightPos);
        int $$1 = Math.max(this.cursorPos, this.highlightPos);
        return this.value.substring($$0, $$1);
    }

    public void setFilter(Predicate<String> filter) {
        this.filter = filter;
    }

    public void insertText(String text) {
        int selectMin = Math.min(this.cursorPos, this.highlightPos);
        int selectMax = Math.max(this.cursorPos, this.highlightPos);
        int maxWidth = this.maxLength - this.value.length() - (selectMin - selectMax);
        if (maxWidth > 0) {
            String filteredText = StringUtil.filterText(text);
            int width = filteredText.length();
            if (maxWidth < width) {
                if (Character.isHighSurrogate(filteredText.charAt(maxWidth - 1))) {
                    --maxWidth;
                }

                filteredText = filteredText.substring(0, maxWidth);
                width = maxWidth;
            }

            String transformedText = (new StringBuilder(this.value)).replace(selectMin, selectMax, filteredText).toString();
            if (this.filter.test(transformedText)) {
                this.value = transformedText;
                this.setCursorPosition(selectMin + width);
                this.setHighlightPos(this.cursorPos);
                this.onValueChange(this.value);
            }
        }
    }

    private void onValueChange(String text) {
        if (this.responder != null) {
            this.responder.accept(text);
        }

    }

    private void deleteText(int count, boolean words) {
        if (words) {
            this.deleteWords(count);
        } else {
            this.deleteChars(count);
        }

    }

    public void deleteWords(int count) {
        if (!this.value.isEmpty()) {
            if (this.highlightPos != this.cursorPos) {
                this.insertText("");
            } else {
                this.deleteCharsToPos(this.getWordPosition(count));
            }
        }
    }

    public void deleteChars(int count) {
        this.deleteCharsToPos(this.getCursorPos(count));
    }

    public void deleteCharsToPos(int pos) {
        if (!this.value.isEmpty()) {
            if (this.highlightPos != this.cursorPos) {
                this.insertText("");
            } else {
                int $$1 = Math.min(pos, this.cursorPos);
                int $$2 = Math.max(pos, this.cursorPos);
                if ($$1 != $$2) {
                    String $$3 = (new StringBuilder(this.value)).delete($$1, $$2).toString();
                    if (this.filter.test($$3)) {
                        this.value = $$3;
                        this.moveCursorTo($$1, false);
                    }
                }
            }
        }
    }

    public int getWordPosition(int $$0) {
        return this.getWordPosition($$0, this.getCursorPosition());
    }

    private int getWordPosition(int $$0, int $$1) {
        return this.getWordPosition($$0, $$1, true);
    }

    private int getWordPosition(int $$0, int $$1, boolean $$2) {
        int $$3 = $$1;
        boolean $$4 = $$0 < 0;
        int $$5 = Math.abs($$0);

        for(int $$6 = 0; $$6 < $$5; ++$$6) {
            if (!$$4) {
                int $$7 = this.value.length();
                $$3 = this.value.indexOf(32, $$3);
                if ($$3 == -1) {
                    $$3 = $$7;
                } else {
                    while($$2 && $$3 < $$7 && this.value.charAt($$3) == ' ') {
                        ++$$3;
                    }
                }
            } else {
                while($$2 && $$3 > 0 && this.value.charAt($$3 - 1) == ' ') {
                    --$$3;
                }

                while($$3 > 0 && this.value.charAt($$3 - 1) != ' ') {
                    --$$3;
                }
            }
        }

        return $$3;
    }

    public void moveCursor(int $$0, boolean $$1) {
        this.moveCursorTo(this.getCursorPos($$0), $$1);
    }

    private int getCursorPos(int $$0) {
        return Util.offsetByCodepoints(this.value, this.cursorPos, $$0);
    }

    public void moveCursorTo(int $$0, boolean $$1) {
        this.setCursorPosition($$0);
        if (!$$1) {
            this.setHighlightPos(this.cursorPos);
        }

        this.onValueChange(this.value);
    }

    public void setCursorPosition(int $$0) {
        this.cursorPos = Mth.clamp($$0, 0, this.value.length());
        this.scrollTo(this.cursorPos);
    }

    public void moveCursorToStart(boolean $$0) {
        this.moveCursorTo(0, $$0);
    }

    public void moveCursorToEnd(boolean $$0) {
        this.moveCursorTo(this.value.length(), $$0);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.isActive() && this.isFocused()) {
            switch (event.key()) {
                case 259:
                    this.deleteText(-1, event.hasControlDown());
                    return true;
                case 260:
                case 264:
                case 265:
                case 266:
                case 267:
                default:
                    if (event.isSelectAll()) {
                        this.moveCursorToEnd(false);
                        this.setHighlightPos(0);
                        return true;
                    } else if (event.isCopy()) {
                        Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
                        return true;
                    } else if (event.isPaste()) {
                        this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());

                        return true;
                    } else {
                        if (event.isCut()) {
                            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
                            this.insertText("");
                            return true;
                        }

                        return false;
                    }
                case 261:
                    this.deleteText(1, event.hasControlDown());
                    return true;
                case 262:
                    if (event.hasControlDown()) {
                        this.moveCursorTo(this.getWordPosition(1), event.hasShiftDown());
                    } else {
                        this.moveCursor(1, event.hasShiftDown());
                    }

                    return true;
                case 263:
                    if (event.hasControlDown()) {
                        this.moveCursorTo(this.getWordPosition(-1), event.hasShiftDown());
                    } else {
                        this.moveCursor(-1, event.hasShiftDown());
                    }

                    return true;
                case 268:
                    this.moveCursorToStart(event.hasShiftDown());
                    return true;
                case 269:
                    this.moveCursorToEnd(event.hasShiftDown());
                    return true;
            }
        } else {
            return false;
        }
    }

    public boolean canConsumeInput() {
        return this.isActive() && this.isFocused();
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (!this.canConsumeInput()) {
            return false;
        } else if (event.isAllowedChatCharacter()) {
            if (this.isActive()) {
                this.insertText(event.codepointAsString());
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        if (doubleClick) {
            this.selectWord(event);
        } else {
            this.moveCursorTo(this.findClickedPositionInText(event), event.hasShiftDown());
        }
    }

    private int findClickedPositionInText(MouseButtonEvent event) {
        int i = Math.min(Mth.floor(event.x()), this.getInnerWidth());
        String s = this.value.substring(this.displayPos);
        return this.displayPos + this.font.plainSubstrByWidth(s, i).length();
    }

    private void selectWord(MouseButtonEvent event) {
        int i = this.findClickedPositionInText(event);
        int j = this.getWordPosition(-1, i);
        int k = this.getWordPosition(1, i);
        this.moveCursorTo(j, false);
        this.moveCursorTo(k, true);
    }

    @Override
    public void playDownSound(SoundManager manager) {
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTick) {
        if (this.isVisible()) {
            if (this.isBordered()) {
                this.renderBackground(graphics);
            }

            int textColor = this.theme.getWidgetTextColor(this.active, this.isHoveredOrFocused());
            int position = this.cursorPos - this.displayPos;
            String displayValue = this.numberFormatter != null && !this.isFocused() ? this.numberFormatter.applyFormat() : this.value;
            String label = this.font.plainSubstrByWidth(displayValue.substring(this.displayPos), this.getInnerWidth());
            boolean cursorAtEnd = position >= 0 && position <= label.length();
            boolean blink = this.isFocused() && (Util.getMillis() - this.focusedTime) / CURSOR_BLINK_INTERVAL_MS % 2L == 0L && cursorAtEnd;
            int left = this.bordered ? this.getX() + 4 : this.getX();
            int top = this.bordered ? this.getY() + (this.height - 8) / 2 : this.getY();
            int textLeft = left;
            int $$13 = Mth.clamp(this.highlightPos - this.displayPos, 0, label.length());
            if (!label.isEmpty()) {
                String renderString = cursorAtEnd ? label.substring(0, position) : label;
                FormattedCharSequence sequence = this.formatter.apply(renderString, this.displayPos);
                graphics.drawString(this.font, sequence, textLeft, top, textColor);
                textLeft += this.font.width(sequence) + 1;
            }

            boolean $$15 = this.cursorPos < displayValue.length() || displayValue.length() >= this.getMaxLength();
            int $$16 = textLeft;
            if (!cursorAtEnd) {
                $$16 = position > 0 ? left + this.width : left;
            } else if ($$15) {
                --$$16;
                --textLeft;
            }

            if (!label.isEmpty() && cursorAtEnd && position < label.length()) {
                graphics.drawString(this.font, this.formatter.apply(label.substring(position), this.cursorPos), textLeft, top, textColor);
            }

            if (this.hint != null && label.isEmpty() && !this.isFocused()) {
                graphics.drawString(this.font, this.hint, textLeft, top, textColor);
            }

            if (!$$15 && this.suggestion != null) {
                graphics.drawString(this.font, this.suggestion, $$16 - 1, top, 0xFF808080);
            }

            int var10003;
            int var10004;
            int var10005;
            if (blink) {
                if ($$15) {
                    var10003 = top - 1;
                    var10004 = $$16 + 1;
                    var10005 = top + 1;
                    graphics.fill($$16, var10003, var10004, var10005 + 9, CURSOR_INSERT_COLOR);
                } else {
                    graphics.drawString(this.font, CURSOR_APPEND_CHARACTER, $$16, top, textColor);
                }
            }

            if ($$13 != position && this.isFocused()) {
                int $$17 = left + this.font.width(label.substring(0, $$13));
                var10003 = top - 1;
                var10004 = $$17 - 1;
                var10005 = top + 1;
                this.renderHighlight(graphics, $$16, var10003, var10004, var10005 + 9);
            }

        }
    }

    private void renderHighlight(GuiGraphics graphics, int x1, int y1, int x2, int y2) {
        int $$6;
        if (x1 < x2) {
            $$6 = x1;
            x1 = x2;
            x2 = $$6;
        }

        if (y1 < y2) {
            $$6 = y1;
            y1 = y2;
            y2 = $$6;
        }

        if (x2 > this.getX() + this.width) {
            x2 = this.getX() + this.width;
        }

        if (x1 > this.getX() + this.width) {
            x1 = this.getX() + this.width;
        }

        graphics.fill(RenderPipelines.GUI_TEXT_HIGHLIGHT, x1, y1, x2, y2, 0xff0000ff);
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        if (this.value.length() > maxLength) {
            this.value = this.value.substring(0, maxLength);
            this.onValueChange(this.value);
        }

    }

    private int getMaxLength() {
        return this.maxLength;
    }

    public int getCursorPosition() {
        return this.cursorPos;
    }

    public boolean isBordered() {
        return this.bordered;
    }

    public void setBordered(boolean bordered) {
        this.bordered = bordered;
    }

    @Override
    public void setFocused(boolean focused) {
        if (this.canLoseFocus || focused) {
            super.setFocused(focused);
            if (focused) {
                this.focusedTime = Util.getMillis();
            }

        }
    }

    public int getInnerWidth() {
        return this.isBordered() ? this.width - 8 : this.width;
    }

    public void setHighlightPos(int pos) {
        this.highlightPos = Mth.clamp(pos, 0, this.value.length());
        this.scrollTo(this.highlightPos);
    }

    private void scrollTo(int position) {
        if (this.font != null) {
            this.displayPos = Math.min(this.displayPos, this.value.length());
            int $$1 = this.getInnerWidth();
            String $$2 = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), $$1);
            int $$3 = $$2.length() + this.displayPos;
            if (position == this.displayPos) {
                this.displayPos -= this.font.plainSubstrByWidth(this.value, $$1, true).length();
            }

            if (position > $$3) {
                this.displayPos += position - $$3;
            } else if (position <= this.displayPos) {
                this.displayPos -= this.displayPos - position;
            }

            this.displayPos = Mth.clamp(this.displayPos, 0, this.value.length());
        }
    }

    public void setCanLoseFocus(boolean canLoseFocus) {
        this.canLoseFocus = canLoseFocus;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public int getScreenX(int $$0) {
        return $$0 > this.value.length() ? this.getX() : this.getX() + this.font.width(this.value.substring(0, $$0));
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, this.createNarrationMessage());
    }

    public void setHint(Component $$0) {
        this.hint = $$0;
    }

    public record NumberFormatter(DecimalFormat format, Supplier<Number> value) {

        public String applyFormat() {
            return format.format(value.get());
        }
    }
}
