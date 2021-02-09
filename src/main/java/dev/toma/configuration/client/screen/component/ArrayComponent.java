package dev.toma.configuration.client.screen.component;

import dev.toma.configuration.api.type.FixedCollectionType;
import dev.toma.configuration.api.util.Nameable;
import net.minecraft.client.gui.FontRenderer;

public class ArrayComponent<V extends Nameable, T extends FixedCollectionType<V>> extends ConfigComponent<T> {

    private int index;

    public ArrayComponent(T type, int x, int y, int width, int height) {
        super(type, x, y, width, height);
        this.index = findIndex();
    }

    @Override
    public void drawComponent(FontRenderer font, int mouseX, int mouseY, float partialTicks, boolean hovered) {
        drawColorShape(x, y, x + width, y + height, 1.0F, 1.0F, 1.0F, 1.0F);
        drawColorShape(x + 1, y + 1, x + width - 1, y + height - 1, 0.0F, 0.0F, 0.0F, 1.0F);
        String displayedText = configType.get().getFormattedName();
        int textWidth = font.getStringWidth(displayedText);
        font.drawStringWithShadow(displayedText, x + (width - textWidth) / 2.0F, y + (height - font.FONT_HEIGHT) / 2.0F, hovered ? 0xFFFF00 : 0xFFFFFF);
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
        String key = configType.get().getUnformattedName();
        V[] objects = configType.collect();
        for (int i = 0; i < objects.length; i++) {
            V v = objects[i];
            if(key.equalsIgnoreCase(v.getUnformattedName())) {
                return i;
            }
        }
        return 0;
    }
}
