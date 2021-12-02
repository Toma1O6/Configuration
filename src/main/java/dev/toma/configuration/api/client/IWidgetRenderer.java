package dev.toma.configuration.api.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.toma.configuration.api.client.widget.Widget;
import net.minecraft.client.Minecraft;

/**
 * Handles rendering of specific widget
 * @param <W> Type of widget
 */
@Deprecated
public interface IWidgetRenderer<W extends Widget> {

    /**
     * Renders widget into UI
     * @param widget Widget to render
     * @param stack Current matrix
     * @param mc Running mc instance
     * @param mouseX X mouse position
     * @param mouseY Y mouse position
     * @param partialTicks Partial render tick time, used for interpolation
     */
    void renderWidget(W widget, PoseStack stack, Minecraft mc, int mouseX, int mouseY, float partialTicks);
}
