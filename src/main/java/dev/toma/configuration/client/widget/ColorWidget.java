package dev.toma.configuration.client.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;

import java.util.function.IntSupplier;

public final class ColorWidget extends AbstractWidget {

    private final boolean argb;
    private final IntSupplier colorSupplier;

    public ColorWidget(int x, int y, int width, int height, boolean argb, IntSupplier colorSupplier) {
        super(x, y, width, height, TextComponent.EMPTY);
        this.argb = argb;
        this.colorSupplier = colorSupplier;
    }

    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialRenderTicks) {
        int borderColor = this.isFocused() ? 0xffffffff : 0xffa0a0a0;
        int providedColor = this.colorSupplier.getAsInt();
        int color = this.argb ? providedColor : (0xFF << 24) | providedColor;
        fill(stack, this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, borderColor);
        fillGradient(stack, this.x, this.y, this.x + this.width, this.y + this.height, 0xFFFFFFFF, 0xFF888888);
        fill(stack, this.x, this.y, this.x + this.width, this.y + this.height, color);
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return false;
    }

    @Override
    public void updateNarration(NarrationElementOutput p_169152_) {
    }
}
