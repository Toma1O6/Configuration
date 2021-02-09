package dev.toma.configuration.api.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.configuration.client.screen.ComponentScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Background renderer handles UI background rendering.
 * You can either implement your own, or use default implementation
 * which will render dirt background which can be seen in vanilla
 * menus.
 *
 * @author Toma
 */
public interface BackgroundRenderer {

    /**
     * Called every render tick from {@link net.minecraft.client.gui.screen.Screen#render(MatrixStack, int, int, float)}
     *
     * @param screen Screen being rendered
     * @param matrixStack {@link MatrixStack} passed into {@link net.minecraft.client.gui.screen.Screen#render(MatrixStack, int, int, float)} method
     * @param mouseX X position of mouse on {@link net.minecraft.client.gui.screen.Screen}
     * @param mouseY Y position of mouse on {@link net.minecraft.client.gui.screen.Screen}
     * @param partialTicks Partial tick parameter for smooth rendering
     */
    @OnlyIn(Dist.CLIENT)
    void drawBackground(ComponentScreen screen, MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks);

    /**
     * Allows you to change color of config entry names
     * when using lighter background textures to maintain
     * text visibility
     *
     * @return color in ARGB format
     */
    default int getTextColor() {
        return 0x999999;
    }

    /**
     * Default implementation of {@link BackgroundRenderer}
     * Renders vanilla dirt background
     */
    class DirtBackground implements BackgroundRenderer {
        public static final DirtBackground INSTANCE = new DirtBackground();

        @OnlyIn(Dist.CLIENT)
        @Override
        public void drawBackground(ComponentScreen screen, MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            screen.renderDirtBackground(0);
        }
    }
}
