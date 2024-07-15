package dev.toma.configuration.client.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.toma.configuration.client.screen.AbstractConfigScreen;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.config.value.BooleanValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

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
        if (this.backgroundRenderer != null) {
            this.backgroundRenderer.draw(graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }
        graphics.blitSprite(AbstractConfigScreen.BUTTON_SPRITES.get(active, isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.active ? 0xffffff : 0xa0a0a0;
        this.renderString(graphics, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    private void renderString(GuiGraphics graphics, Font font, int color) {
        this.renderScrollingString(graphics, font, 2, color);
    }

    @Override
    public void onClick(double x, double y) {
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
