package dev.toma.configuration.api.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.configuration.api.client.widget.Widget;
import net.minecraft.client.Minecraft;

public interface IWidgetRenderer<W extends Widget> {

    void renderWidget(W widget, MatrixStack stack, Minecraft mc, int mouseX, int mouseY, float partialTicks);
}
