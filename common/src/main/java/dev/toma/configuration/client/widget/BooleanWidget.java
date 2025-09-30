package dev.toma.configuration.client.widget;

import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.config.value.BooleanValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class BooleanWidget extends AbstractThemeWidget {

    private final BooleanValue value;
    private final Component trueLabel, falseLabel;

    public BooleanWidget(int x, int y, int w, int h, ConfigTheme theme, BooleanValue value, Component trueLabel, Component falseLabel) {
        super(x, y, w, h, theme);
        this.value = value;
        this.trueLabel = trueLabel;
        this.falseLabel = falseLabel;
        this.readState();
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        this.renderBackground(graphics);
        int textColor = this.theme.getWidgetTextColor(this.active, this.isHovered);
        this.renderString(graphics, minecraft.font, textColor);
    }

    private void renderString(GuiGraphics graphics, Font font, int color) {
        this.renderScrollingString(graphics, font, 2, color);
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        this.setState(!this.value.get());
    }

    private void readState() {
        boolean value = this.value.get();
        this.setMessage(value ? this.trueLabel : this.falseLabel);
    }

    public void setState(boolean state) {
        this.value.setValue(state);
        this.setChanged();
        this.readState();
    }
}
