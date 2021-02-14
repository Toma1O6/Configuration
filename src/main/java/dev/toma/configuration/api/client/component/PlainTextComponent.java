package dev.toma.configuration.api.client.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;

public class PlainTextComponent extends Component {

    final int textColor;
    final String displayText;

    public PlainTextComponent(int x, int y, int width, int height, int textColor, String text) {
        super(x, y, width, height);
        this.textColor = textColor;
        this.displayText = text;
    }

    @Override
    public boolean hasClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public void drawComponent(MatrixStack matrixStack, FontRenderer font, int mouseX, int mouseY, float partialTicks, boolean hovered) {
        font.drawStringWithShadow(matrixStack, displayText, x + 2, y + 6, textColor);
    }
}
