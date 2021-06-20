package dev.toma.configuration.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.configuration.api.client.HorizontalAlignment;
import dev.toma.configuration.api.client.VerticalAlignment;
import dev.toma.configuration.api.client.widget.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;

public final class WidgetRenderers {

    public static void renderLabel(LabelWidget widget, MatrixStack stack, Minecraft mc, int mouseX, int mouseY, float delta) {
        FontRenderer renderer = mc.fontRenderer;
        ITextComponent component = widget.content;
        String text = component.getString();
        float left = widget.horizontalAlignment.getHorizontalPos(widget.getX(), widget.getWidth(), renderer.getStringWidth(text));
        float top = widget.verticalAlignment.getVerticalPos(widget.getY(), widget.getHeight(), renderer.FONT_HEIGHT);
        renderer.drawStringWithShadow(stack, text, left, top, widget.foreground);
    }

    public static void renderButton(ButtonWidget widget, MatrixStack stack, Minecraft mc, int mouseX, int mouseY, float delta) {
        FontRenderer renderer = mc.fontRenderer;
        int x1 = widget.getX();
        int y1 = widget.getY();
        int x2 = x1 + widget.getWidth();
        int y2 = y1 + widget.getHeight();
        Widget.drawColorShape(stack, x1, y1, x2, y2, widget.isMouseOver(mouseX, mouseY) ? 0xFFFFFF00 : widget.borderColor);
        Widget.drawColorShape(stack, x1 + 1, y1 + 1, x2 - 1, y2 - 1, widget.background);
        ITextComponent content = widget.text;
        if (content != null) {
            Widget.drawAlignedString(content.getString(), stack, renderer, x1, y1, widget.getWidth(), widget.getHeight(), widget.foreground, widget.horizontalAlignment, widget.verticalAlignment);
        }
    }

    public static void renderBinaryButton(TwoStateButton widget, MatrixStack stack, Minecraft mc, int mouseX, int mouseY, float delta) {
        FontRenderer renderer = mc.fontRenderer;
        String value = widget.getContent();
        int x1 = widget.getX();
        int y1 = widget.getY();
        int x2 = x1 + widget.getWidth();
        int y2 = y1 + widget.getHeight();
        Widget.drawColorShape(stack, x1, y1, x2, y2, widget.isMouseOver(mouseX, mouseY) ? 0xFFFFFF00 : widget.borderColor);
        Widget.drawColorShape(stack, x1 + 1, y1 + 1, x2 - 1, y2 - 1, widget.background);
        Widget.drawCenteredString(value, stack, renderer, x1, y1, widget.getWidth(), widget.getHeight(), widget.foreground);
    }

    public static void renderArrayButton(ArrayButtonWidget widget, MatrixStack stack, Minecraft mc, int mouseX, int mouseY, float delta) {
        renderDefaultButton(mc.fontRenderer, stack, widget.isMouseOver(mouseX, mouseY), widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight(), widget.border, widget.background, widget.foreground, widget.horizontalAlignment, widget.verticalAlignment, widget.getConfigType().getId());
    }

    public static void renderCollectionButton(CollectionButton<?> widget, MatrixStack stack, Minecraft mc, int mouseX, int mouseY, float delta) {
        renderDefaultButton(mc.fontRenderer, stack, widget.isMouseOver(mouseX, mouseY), widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight(), widget.borderColor, widget.background, widget.foreground, widget.horizontalAlignment, widget.verticalAlignment, widget.getConfigType().getId());
    }

    public static void renderObjectButton(ObjectTypeWidget widget, MatrixStack stack, Minecraft mc, int mouseX, int mouseY, float delta) {
        renderDefaultButton(mc.fontRenderer, stack, widget.isMouseOver(mouseX, mouseY), widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight(), widget.border, widget.background, widget.foreground, widget.horizontalAlignment, widget.verticalAlignment, widget.getConfigType().getId());
    }

    private static void renderDefaultButton(FontRenderer renderer, MatrixStack stack, boolean hovered, int x, int y, int width, int height, int border, int background, int foreground, HorizontalAlignment hAlignment, VerticalAlignment vAlignment, String text) {
        Widget.drawColorShape(stack, x, y, x + width, y + height, hovered ? 0xFFFFFF00 : border);
        Widget.drawColorShape(stack, x + 1, y + 1, x + width - 1, y + height - 1, background);
        Widget.drawAlignedString(text, stack, renderer, x, y, width, height, foreground, hAlignment, vAlignment);
    }
}
