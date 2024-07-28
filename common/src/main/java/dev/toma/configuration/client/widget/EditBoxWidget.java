package dev.toma.configuration.client.widget;

import dev.toma.configuration.client.theme.ConfigTheme;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
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
public class EditBoxWidget extends AbstractThemeWidget {

    public static final WidgetSprites SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("widget/text_field"), ResourceLocation.withDefaultNamespace("widget/text_field_highlighted"));
    private static final int CURSOR_INSERT_WIDTH = 1;
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

    private void deleteText(int count) {
        if (Screen.hasControlDown()) {
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

    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (this.isActive() && this.isFocused()) {
            switch ($$0) {
                case 259:
                    this.deleteText(-1);
                    return true;
                case 260:
                case 264:
                case 265:
                case 266:
                case 267:
                default:
                    if (Screen.isSelectAll($$0)) {
                        this.moveCursorToEnd(false);
                        this.setHighlightPos(0);
                        return true;
                    } else if (Screen.isCopy($$0)) {
                        Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
                        return true;
                    } else if (Screen.isPaste($$0)) {
                        this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());

                        return true;
                    } else {
                        if (Screen.isCut($$0)) {
                            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
                            this.insertText("");
                            return true;
                        }

                        return false;
                    }
                case 261:
                    this.deleteText(1);
                    return true;
                case 262:
                    if (Screen.hasControlDown()) {
                        this.moveCursorTo(this.getWordPosition(1), Screen.hasShiftDown());
                    } else {
                        this.moveCursor(1, Screen.hasShiftDown());
                    }

                    return true;
                case 263:
                    if (Screen.hasControlDown()) {
                        this.moveCursorTo(this.getWordPosition(-1), Screen.hasShiftDown());
                    } else {
                        this.moveCursor(-1, Screen.hasShiftDown());
                    }

                    return true;
                case 268:
                    this.moveCursorToStart(Screen.hasShiftDown());
                    return true;
                case 269:
                    this.moveCursorToEnd(Screen.hasShiftDown());
                    return true;
            }
        } else {
            return false;
        }
    }

    public boolean canConsumeInput() {
        return this.isActive() && this.isFocused();
    }

    public boolean charTyped(char $$0, int $$1) {
        if (!this.canConsumeInput()) {
            return false;
        } else if (StringUtil.isAllowedChatCharacter($$0)) {
            if (this.isActive()) {
                this.insertText(Character.toString($$0));
            }

            return true;
        } else {
            return false;
        }
    }

    public void onClick(double $$0, double $$1) {
        int $$2 = Mth.floor($$0) - this.getX();
        if (this.bordered) {
            $$2 -= 4;
        }

        String $$3 = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
        this.moveCursorTo(this.font.plainSubstrByWidth($$3, $$2).length() + this.displayPos, Screen.hasShiftDown());
    }

    public void playDownSound(SoundManager $$0) {
    }

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
            int $$12 = left;
            int $$13 = Mth.clamp(this.highlightPos - this.displayPos, 0, label.length());
            if (!label.isEmpty()) {
                String $$14 = cursorAtEnd ? label.substring(0, position) : label;
                $$12 = graphics.drawString(this.font, this.formatter.apply($$14, this.displayPos), $$12, top, textColor);
            }

            boolean $$15 = this.cursorPos < displayValue.length() || displayValue.length() >= this.getMaxLength();
            int $$16 = $$12;
            if (!cursorAtEnd) {
                $$16 = position > 0 ? left + this.width : left;
            } else if ($$15) {
                --$$16;
                --$$12;
            }

            if (!label.isEmpty() && cursorAtEnd && position < label.length()) {
                graphics.drawString(this.font, this.formatter.apply(label.substring(position), this.cursorPos), $$12, top, textColor);
            }

            if (this.hint != null && label.isEmpty() && !this.isFocused()) {
                graphics.drawString(this.font, this.hint, $$12, top, textColor);
            }

            if (!$$15 && this.suggestion != null) {
                graphics.drawString(this.font, this.suggestion, $$16 - 1, top, -8355712);
            }

            int var10003;
            int var10004;
            int var10005;
            if (blink) {
                if ($$15) {
                    RenderType var10001 = RenderType.guiOverlay();
                    var10003 = top - 1;
                    var10004 = $$16 + 1;
                    var10005 = top + 1;
                    Objects.requireNonNull(this.font);
                    graphics.fill(var10001, $$16, var10003, var10004, var10005 + 9, CURSOR_INSERT_COLOR);
                } else {
                    graphics.drawString(this.font, CURSOR_APPEND_CHARACTER, $$16, top, textColor);
                }
            }

            if ($$13 != position && this.isFocused()) {
                int $$17 = left + this.font.width(label.substring(0, $$13));
                var10003 = top - 1;
                var10004 = $$17 - 1;
                var10005 = top + 1;
                Objects.requireNonNull(this.font);
                this.renderHighlight(graphics, $$16, var10003, var10004, var10005 + 9);
            }

        }
    }

    private void renderHighlight(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4) {
        int $$6;
        if ($$1 < $$3) {
            $$6 = $$1;
            $$1 = $$3;
            $$3 = $$6;
        }

        if ($$2 < $$4) {
            $$6 = $$2;
            $$2 = $$4;
            $$4 = $$6;
        }

        if ($$3 > this.getX() + this.width) {
            $$3 = this.getX() + this.width;
        }

        if ($$1 > this.getX() + this.width) {
            $$1 = this.getX() + this.width;
        }

        $$0.fill(RenderType.guiTextHighlight(), $$1, $$2, $$3, $$4, -16776961);
    }

    public void setMaxLength(int $$0) {
        this.maxLength = $$0;
        if (this.value.length() > $$0) {
            this.value = this.value.substring(0, $$0);
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

    public void setBordered(boolean $$0) {
        this.bordered = $$0;
    }

    public void setFocused(boolean $$0) {
        if (this.canLoseFocus || $$0) {
            super.setFocused($$0);
            if ($$0) {
                this.focusedTime = Util.getMillis();
            }

        }
    }

    public int getInnerWidth() {
        return this.isBordered() ? this.width - 8 : this.width;
    }

    public void setHighlightPos(int $$0) {
        this.highlightPos = Mth.clamp($$0, 0, this.value.length());
        this.scrollTo(this.highlightPos);
    }

    private void scrollTo(int $$0) {
        if (this.font != null) {
            this.displayPos = Math.min(this.displayPos, this.value.length());
            int $$1 = this.getInnerWidth();
            String $$2 = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), $$1);
            int $$3 = $$2.length() + this.displayPos;
            if ($$0 == this.displayPos) {
                this.displayPos -= this.font.plainSubstrByWidth(this.value, $$1, true).length();
            }

            if ($$0 > $$3) {
                this.displayPos += $$0 - $$3;
            } else if ($$0 <= this.displayPos) {
                this.displayPos -= this.displayPos - $$0;
            }

            this.displayPos = Mth.clamp(this.displayPos, 0, this.value.length());
        }
    }

    public void setCanLoseFocus(boolean $$0) {
        this.canLoseFocus = $$0;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean $$0) {
        this.visible = $$0;
    }

    public void setSuggestion(String $$0) {
        this.suggestion = $$0;
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
