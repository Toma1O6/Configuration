package dev.toma.configuration.api.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import dev.toma.configuration.api.client.ClientHandles;
import dev.toma.configuration.api.client.ComponentFactory;
import dev.toma.configuration.api.client.component.AddCollectionElementComponent;
import dev.toma.configuration.api.client.component.Component;
import dev.toma.configuration.api.client.component.RemoveCollectionElementComponent;
import dev.toma.configuration.api.type.AbstractConfigType;
import dev.toma.configuration.api.type.CollectionType;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class CollectionScreen<T extends AbstractConfigType<?>> extends ComponentScreen {

    final Screen screen;
    final CollectionType<T> type;
    int displayCount;
    int scrollIndex;

    public CollectionScreen(Screen parentScreen, CollectionType<T> type, String modid, ClientHandles handles) {
        super(new StringTextComponent(type.getId() != null ? type.getId() : "Unnamed collection"), modid, handles);
        this.screen = parentScreen;
        this.type = type;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int newIndex = scrollIndex - (int) delta;
        int cfgElements = type.get().size();
        if(newIndex != scrollIndex && newIndex >= 0 && newIndex <= cfgElements - displayCount) {
            scrollIndex = newIndex;
            init(minecraft, width, height);
            return true;
        }
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
        minecraft.displayGuiScreen(screen);
    }

    @Override
    protected void init() {
        displayCount = ((height - 40) / 25) - 1;
        List<T> list = type.get();
        if(scrollIndex > list.size() - displayCount) {
            scrollIndex = Math.max(0, list.size() - displayCount);
        }
        int end = Math.min(scrollIndex + displayCount, list.size());
        for (int i = scrollIndex; i < end; i++) {
            int offset = i - scrollIndex;
            T type = list.get(i);
            ComponentFactory display = type.getComponentFactory();
            display.addComponents(this, type, 30, 35 + offset * 25, width - 85, 20);
            addComponent(new RemoveCollectionElementComponent(this, this.type, i, width - 50, 35 + offset * 25, 20, 20));
        }
        int posY = 35 + Math.min(end, displayCount) * 25;
        addComponent(new AddCollectionElementComponent<>(this, type, 30, posY, width - 60, 20));
    }

    @Override
    public void renderScreenPost(int mouseX, int mouseY, float partialTicks) {
        this.renderHeader(font);
        int count = type.get().size();
        if(count > displayCount) {
            this.renderScrollbar(count);
        }
        this.renderHoveredInfo(mouseX, mouseY);
    }

    void renderScrollbar(int count) {
        int height = ((1 + displayCount) * 25) - 5;
        double step = height / (double) count;
        double start = 35 + scrollIndex * step;
        double end = 35 + (scrollIndex + displayCount) * step;
        int left = width - 20;
        int right = width - 10;
        Component.drawColorShape(left, 35, right, 35 + height, 0.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.disableTexture();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        builder.pos(left, end, 0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        builder.pos(right, end, 0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        builder.pos(right, start, 0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        builder.pos(left, start, 0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture();
    }

    void renderHeader(FontRenderer renderer) {
        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        int headerHeight = 20;
        float headerAlpha = 0.4F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        builder.pos(0, headerHeight, 0).color(0.0F, 0.0F, 0.0F, headerAlpha).endVertex();
        builder.pos(width, headerHeight, 0).color(0.0F, 0.0F, 0.0F, headerAlpha).endVertex();
        builder.pos(width, 0, 0).color(0.0F, 0.0F, 0.0F, headerAlpha).endVertex();
        builder.pos(0, 0, 0).color(0.0F, 0.0F, 0.0F, headerAlpha).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture();

        String headerText = TextFormatting.BOLD + title.getUnformattedComponentText();
        int headerTextWidth = renderer.getStringWidth(headerText);
        renderer.drawStringWithShadow(headerText, (width - headerTextWidth) / 2f, (headerHeight - renderer.FONT_HEIGHT) / 2f, 0xFFFFFF);
    }
}
