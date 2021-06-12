package dev.toma.configuration.api.client.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.configuration.api.type.ArrayType;
import net.minecraft.client.gui.FontRenderer;

public class ArrayComponent<V, T extends ArrayType<V>> extends ConfigComponent<T> {

    private int index;

    public ArrayComponent(T type, int x, int y, int width, int height) {
        super(type, x, y, width, height);
        this.index = findIndex();
    }

    @Override
    public void drawComponent(MatrixStack matrixStack, FontRenderer font, int mouseX, int mouseY, float partialTicks, boolean hovered) {
        drawColorShape(matrixStack, x, y, x + width, y + height, 1.0F, 1.0F, 1.0F, 1.0F);
        drawColorShape(matrixStack, x + 1, y + 1, x + width - 1, y + height - 1, 0.0F, 0.0F, 0.0F, 1.0F);
        String displayedText = configType.getElementDisplayName(configType.get());
        int textWidth = font.getStringWidth(displayedText);
        font.drawStringWithShadow(matrixStack, displayedText, x + (width - textWidth) / 2.0F, y + (height - font.FONT_HEIGHT) / 2.0F, hovered ? 0xFFFF00 : 0xFFFFFF);
    }

    @Override
    public void processClicked(double mouseX, double mouseY) {
        int newIndex = index + 1;
        V[] array = configType.collect();
        int length = array.length;
        if(newIndex >= length) {
            newIndex = 0;
        }
        this.index = newIndex;
        this.configType.set(array[index]);
    }

    int findIndex() {
        V value = configType.get();
        String key = configType.getElementKey(value);
        V[] objects = configType.collect();
        for (int i = 0; i < objects.length; i++) {
            V v = objects[i];
            if(key.equalsIgnoreCase(configType.getElementKey(value))) {
                return i;
            }
        }
        return 0;
    }
}
