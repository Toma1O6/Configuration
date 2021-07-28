package dev.toma.configuration.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.toma.configuration.api.client.HorizontalAlignment;
import dev.toma.configuration.api.client.VerticalAlignment;
import dev.toma.configuration.api.client.screen.WidgetScreen;
import dev.toma.configuration.api.client.widget.*;
import dev.toma.configuration.api.type.ArrayType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class WidgetRenderers {

    public static void renderLabel(LabelWidget widget, PoseStack stack, Minecraft mc, int mouseX, int mouseY, float delta) {
        Font renderer = mc.font;
        Component component = widget.content;
        String text = renderer.plainSubstrByWidth(component.getString(), widget.getWidth());
        float left = widget.horizontalAlignment.getHorizontalPos(widget.getX(), widget.getWidth(), renderer.width(text));
        float top = widget.verticalAlignment.getVerticalPos(widget.getY(), widget.getHeight(), renderer.lineHeight);
        renderer.drawShadow(stack, text, left, top, widget.foreground);
    }

    public static void renderButton(ButtonWidget widget, PoseStack stack, Minecraft mc, int mouseX, int mouseY, float delta) {
        Font renderer = mc.font;
        int x1 = widget.getX();
        int y1 = widget.getY();
        int x2 = x1 + widget.getWidth();
        int y2 = y1 + widget.getHeight();
        Widget.drawColorShape(stack, x1, y1, x2, y2, widget.isMouseOver(mouseX, mouseY) ? 0xFFFFFF00 : widget.borderColor);
        Widget.drawColorShape(stack, x1 + 1, y1 + 1, x2 - 1, y2 - 1, widget.background);
        Component content = widget.text;
        if (content != null) {
            Widget.drawAlignedString(content.getString(), stack, renderer, x1, y1, widget.getWidth(), widget.getHeight(), widget.foreground, widget.horizontalAlignment, widget.verticalAlignment);
        }
    }

    public static void renderBinaryButton(TwoStateButton widget, PoseStack stack, Minecraft mc, int mouseX, int mouseY, float delta) {
        Font renderer = mc.font;
        String value = widget.getContent();
        int x1 = widget.getX();
        int y1 = widget.getY();
        int x2 = x1 + widget.getWidth();
        int y2 = y1 + widget.getHeight();
        Widget.drawColorShape(stack, x1, y1, x2, y2, widget.isMouseOver(mouseX, mouseY) ? 0xFFFFFF00 : widget.borderColor);
        Widget.drawColorShape(stack, x1 + 1, y1 + 1, x2 - 1, y2 - 1, widget.background);
        Widget.drawCenteredString(value, stack, renderer, x1, y1, widget.getWidth(), widget.getHeight(), widget.foreground);
    }

    public static <A> void renderArrayButton(ArrayButtonWidget widget, PoseStack stack, Minecraft mc, int mouseX, int mouseY, float delta) {
        ArrayType<A> type = (ArrayType<A>) widget.getConfigType();
        A value = type.get();
        String text = type.getElementDisplayName(value);
        renderDefaultButton(mc.font, stack, widget.isMouseOver(mouseX, mouseY), widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight(), widget.border, widget.background, widget.foreground, widget.horizontalAlignment, widget.verticalAlignment, text);
    }

    public static void renderCollectionButton(CollectionButton<?> widget, PoseStack stack, Minecraft mc, int mouseX, int mouseY, float delta) {
        renderDefaultButton(mc.font, stack, widget.isMouseOver(mouseX, mouseY), widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight(), widget.borderColor, widget.background, widget.foreground, widget.horizontalAlignment, widget.verticalAlignment, widget.getConfigType().getId());
    }

    public static void renderObjectButton(ObjectTypeWidget widget, PoseStack stack, Minecraft mc, int mouseX, int mouseY, float delta) {
        renderDefaultButton(mc.font, stack, widget.isMouseOver(mouseX, mouseY), widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight(), widget.border, widget.background, widget.foreground, widget.horizontalAlignment, widget.verticalAlignment, widget.getConfigType().getId());
    }

    public static void renderTextField(InputWidget<?, ?> widget, PoseStack stack, Minecraft mc, int mouseX, int mouseY, float delta) {
        int x = widget.getX();
        int y = widget.getY();
        int width = widget.getWidth();
        int height = widget.getHeight();
        Widget.drawColorShape(stack, x, y, x + width, y + height, widget.isValid ? (widget.listening || widget.isMouseOver(mouseX, mouseY)) ? widget.listeningBorderColor : widget.borderColor : widget.invalidBorderColor);
        Widget.drawColorShape(stack, x + 1, y + 1, x + width - 1, y + height - 1, widget.background);

        String rawText = widget.getText();
        int textFieldWidth = widget.getWidth(-widget.padding * 2);
        String text = mc.font.plainSubstrByWidth(rawText.substring(widget.characterOffset), textFieldWidth);
        mc.font.drawShadow(stack, widget.isValid ? text : ChatFormatting.ITALIC + text, x + widget.padding, y + (height - mc.font.lineHeight) / 2.0f, widget.isValid ? widget.foreground : 0xFFAA4444);

        // cursor
        if (widget.listening && widget.cursorTick % 20 < 10) {
            int cursorIndex = widget.cursorIndex - widget.characterOffset;
            String sub = widget.getText().substring(0, cursorIndex);
            int subWidth = mc.font.width(sub);
            int cursorLeft = x + widget.padding + subWidth;
            Widget.drawColorShape(stack, cursorLeft, y + 4, cursorLeft + 1, y + 15, 0xFFFFFFFF);
        }

        if (!widget.isValid) {
            // show error
            List<Component> error = widget.getErrorMessage();
            if (!error.isEmpty()) {
                WidgetScreen<?> parent = widget.parent;
                stack.pushPose();
                stack.translate(0, 0, 2);
                parent.renderComponentToolTip(stack, error, widget.getX() - 15, widget.getY() - 12 * (error.size() - 1), mc.font);
                stack.popPose();
            }
        }
    }

    public static void renderSlider(SliderWidget<?, ?> widget, PoseStack stack, Minecraft mc, int mouseX, int mouseY, float delta) {
        float value = widget.sliderValue;
        int x = widget.getX();
        int y = widget.getY();
        int width = widget.getWidth();
        int height = widget.getHeight();

        int y2 = y + height / 3;
        int x2 = (int) (width * value);

        Widget.drawColorShape(stack, x, y2, x + x2, y + height, widget.setBorderColor);
        Widget.drawColorShape(stack, x + x2, y2, x + width, y + height, widget.unsetBorderColor);
        Widget.drawColorShape(stack, x + 1, y2, x + width - 1, y + height - 1, widget.background);

        int sliderWidth = 4;
        int sliderTrailWidth = width - sliderWidth;
        int sliderLeft = x + (int) (value * sliderTrailWidth);

        boolean colorFlag = widget.dragging || widget.isMouseOver(mouseX, mouseY);
        Widget.drawColorShape(stack, sliderLeft, y, sliderLeft + sliderWidth, y + height, colorFlag ? 0xFFFFFF00 : widget.background);
        Widget.drawColorShape(stack, sliderLeft + 1, y + 1, sliderLeft + sliderWidth - 1, y + height - 1, 0xFFFFFFFF);

        if (widget.showValue) {
            String text = widget.formatter.apply(widget.getConfigType().get().toString());
            Widget.drawCenteredString(text, stack, mc.font, x, y + 3, width, height, widget.foreground);
        }
    }

    public static void renderColorDisplay(ColorDisplayWidget widget, PoseStack stack, Minecraft mc, int mouseX, int mouseY, float delta) {
        int x = widget.getX();
        int y = widget.getY();
        int width = widget.getWidth();
        int height = widget.getHeight();
        Widget.drawColorShape(stack, x, y, x + width, y + height, widget.background);
        RenderSystem.setShaderTexture(0, widget.backgroundTexture);
        Widget.drawTexturedShape(stack, x + 1, y + 1, x + width - 1, y + height - 1);
        int color = widget.getConfigType().getColor();
        Widget.drawColorShape(stack, x + 1, y + 1, x + width - 1, y + height - 1, color);
    }

    private static void renderDefaultButton(Font renderer, PoseStack stack, boolean hovered, int x, int y, int width, int height, int border, int background, int foreground, HorizontalAlignment hAlignment, VerticalAlignment vAlignment, String text) {
        Widget.drawColorShape(stack, x, y, x + width, y + height, hovered ? 0xFFFFFF00 : border);
        Widget.drawColorShape(stack, x + 1, y + 1, x + width - 1, y + height - 1, background);
        Widget.drawAlignedString(text, stack, renderer, x, y, width, height, foreground, hAlignment, vAlignment);
    }
}
