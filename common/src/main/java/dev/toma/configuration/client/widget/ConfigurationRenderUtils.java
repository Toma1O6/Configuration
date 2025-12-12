package dev.toma.configuration.client.widget;

import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

public final class ConfigurationRenderUtils {

    public static void renderScrollingString(AbstractWidget container, Component text, int color, int xOffset, ActiveTextCollector textCollector) {
        int left = container.getX() + xOffset;
        int right = container.getRight() - xOffset;
        int top = container.getY();
        int bottom = container.getBottom();
        Component coloredText = text.copy().withStyle(style -> style.withColor(color));
        textCollector.acceptScrolling(coloredText, left, left, right, top, bottom, textCollector.defaultParameters());
    }

    public static void renderCenteredScrollingString(AbstractWidget container, Component text, int color, int xOffset, ActiveTextCollector textCollector) {
        int left = container.getX() + xOffset;
        int right = container.getRight() - xOffset;
        int top = container.getY();
        int bottom = container.getBottom();
        Component coloredText = text.copy().withStyle(style -> style.withColor(color));
        textCollector.acceptScrollingWithDefaultCenter(coloredText, left, right, top, bottom);
    }

    public static ActiveTextCollector createColoredTextCollector(GuiGraphics graphics, int color) {
        return graphics.textRenderer(GuiGraphics.HoveredTextEffects.NONE, style -> style.withColor(color));
    }
}
