package dev.toma.configuration.api.client.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.configuration.api.client.screen.ComponentScreen;
import dev.toma.configuration.api.type.AbstractConfigType;
import dev.toma.configuration.internal.Formatting;
import dev.toma.configuration.internal.Ranged;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.math.MathHelper;

import java.util.function.BiConsumer;

public class SliderComponent<T extends AbstractConfigType<? extends Number> & Ranged<?>> extends ConfigComponent<T> {

    final ComponentScreen parentScreen;
    final boolean shouldShowData;
    final BiConsumer<T, Double> setter;
    double sliderPos;

    public SliderComponent(ComponentScreen parentScreen, T type, boolean shouldShowData, int x, int y, int width, int height, BiConsumer<T, Double> setter) {
        super(type, x, y, width, height);
        this.parentScreen = parentScreen;
        this.shouldShowData = shouldShowData;
        this.setter = setter;
        sliderPos = this.getPosition();
    }

    @Override
    public void drawComponent(MatrixStack matrixStack, FontRenderer font, int mouseX, int mouseY, float partialTicks, boolean hovered) {
        drawColorShape(matrixStack, x, y + 2, x + width, y + height - 2, 1.0F, 1.0F, 1.0F, 1.0F);
        drawColorShape(matrixStack, x + 1, y + 3, x + width - 1, y + height - 3, 0.0F, 0.0F, 0.0F, 1.0F);
        int sliderTrailWidth = width - 4;
        int sliderX = x + 2 + (int)(sliderTrailWidth * sliderPos);
        drawColorShape(matrixStack, sliderX - 2, y, sliderX + 2, y + height, 0.2F, 0.2F, 0.2F, 1.0F);
        drawColorShape(matrixStack, sliderX - 1, y + 1, sliderX + 1, y + height - 1, 0.4F, 0.4F, 0.4F, 1.0F);
        if(shouldShowData) {
            String text = configType.get().toString();
            if(configType instanceof Formatting<?>) {
                text = ((Formatting<?>) configType).getFormatted();
            }
            int tw = font.getStringWidth(text);
            font.drawString(matrixStack, text, x + (width - tw) / 2.0F, 1 + y + (height - font.FONT_HEIGHT) / 2.0F, 0xFFFFFF);
        }
    }

    @Override
    public void mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if(button == 0) {
            this.setSliderOnMouse(mouseX, mouseY);
        }
    }

    @Override
    public void processClicked(double mouseX, double mouseY) {
        this.setSliderOnMouse(mouseX, mouseY);
    }

    void setSliderOnMouse(double mouseX, double mouseY) {
        int diffX = (int) (mouseX - (this.x + 2));
        sliderPos = MathHelper.clamp((float) diffX / (this.width - 4), 0.0, 1.0);
        this.setter.accept(configType, sliderPos);
        this.updateListeners();
    }

    public double getPosition() {
        double min = configType.getMin().doubleValue();
        double max = configType.getMax().doubleValue();
        double at  = configType.get().doubleValue();
        return (at - min) / (max - min);
    }

    public void updatePosition() {
        this.sliderPos = this.getPosition();
    }
}
