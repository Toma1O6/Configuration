package dev.toma.configuration.client.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.config.value.EnumValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class EnumWidget<E extends Enum<E>> extends ThemedButtonWidget {

    private final EnumValue<E> value;

    public EnumWidget(int x, int y, int w, int h, ConfigTheme theme, EnumValue<E> value) {
        super(x, y, w, h, CommonComponents.EMPTY, theme);
        this.value = value;
        this.updateText();
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        this.renderBackground(graphics);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int textColor = this.theme.getWidgetTextColor(this.active, this.isHovered);
        this.renderString(graphics, minecraft.font, textColor);
    }

    private void renderString(GuiGraphics graphics, Font font, int color) {
        this.renderScrollingString(graphics, font, 2, color);
    }

    @Override
    public void onClick(double p_230982_1_, double p_230982_3_) {
        this.nextValue();
        this.updateText();
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput p_169152_) {
    }

    public void setValue(E value) {
        this.value.setValue(value);
        this.updateText();
        this.setChanged();
    }

    private void nextValue() {
        E e = this.value.get();
        E[] values = e.getDeclaringClass().getEnumConstants();
        int i = e.ordinal();
        int j = (i + 1) % values.length;
        E next = values[j];
        this.value.setValue(next);
        this.setChanged();
    }

    private void updateText() {
        E e = this.value.get();
        this.setMessage(Component.literal(e.name()));
    }
}
