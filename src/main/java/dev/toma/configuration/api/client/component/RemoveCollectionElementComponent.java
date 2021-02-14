package dev.toma.configuration.api.client.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.configuration.api.type.CollectionType;
import dev.toma.configuration.api.client.screen.ComponentScreen;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.TextFormatting;

public class RemoveCollectionElementComponent extends Component {

    final ComponentScreen screen;
    final CollectionType<?> type;
    final int elementIndex;

    public RemoveCollectionElementComponent(ComponentScreen screen, CollectionType<?> type, int index, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.screen = screen;
        this.type = type;
        this.elementIndex = index;
    }

    @Override
    public void processClicked(double mouseX, double mouseY) {
        type.remove(elementIndex);
        screen.scheduleUpdate(screen1 -> screen1.init(screen1.getMinecraft(), screen1.width, screen1.height));
    }

    @Override
    public void drawComponent(MatrixStack matrixStack, FontRenderer font, int mouseX, int mouseY, float partialTicks, boolean hovered) {
        drawColorShape(matrixStack, x, y, x + width, y + height, 1.0F, 1.0F, 1.0F, 1.0F);
        drawColorShape(matrixStack, x + 1, y + 1, x + width - 1, y + height - 1, 0.0F, 0.0F, 0.0F, 1.0F);
        int tw = font.getStringWidth(TextFormatting.BOLD + "-");
        font.drawStringWithShadow(matrixStack, TextFormatting.BOLD + "-", x + (width - tw) / 2.0F, y + (height - font.FONT_HEIGHT) / 2.0F, hovered ? 0xFFFF00 : 0xFFFFFF);
    }
}
