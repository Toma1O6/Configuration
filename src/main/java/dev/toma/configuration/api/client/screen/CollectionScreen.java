package dev.toma.configuration.api.client.screen;

import dev.toma.configuration.api.client.ComponentFactory;
import dev.toma.configuration.api.client.component.AddCollectionElementComponent;
import dev.toma.configuration.api.client.component.Component;
import dev.toma.configuration.api.client.component.RemoveCollectionElementComponent;
import dev.toma.configuration.api.type.AbstractConfigType;
import dev.toma.configuration.api.type.CollectionType;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.List;

public class CollectionScreen<T extends AbstractConfigType<?>> extends ComponentScreen {

    final String title;
    final CollectionType<T> type;
    int displayCount;
    int scrollIndex;

    public CollectionScreen(GuiScreen parentScreen, CollectionType<T> type, String modid, int textColor) {
        super(parentScreen, modid, textColor);
        this.title = type.getId() != null ? type.getId() : "Unnamed collection";
        this.type = type;
    }

    @Override
    public void handleMouseInput() throws IOException {
        int delta = Integer.signum(Mouse.getEventDWheel());
        int newIndex = scrollIndex - delta;
        int cfgElements = type.get().size();
        if(newIndex != scrollIndex && newIndex >= 0 && newIndex <= cfgElements - displayCount) {
            scrollIndex = newIndex;
            initGui();
        }
        super.handleMouseInput();
    }

    @Override
    public void initGui() {
        super.initGui();
        displayCount = ((height - 40) / 25) - 1;
        List<T> list = type.get();
        if(scrollIndex > list.size() - displayCount) {
            scrollIndex = Math.max(0, list.size() - displayCount);
        }
        int end = Math.min(scrollIndex + displayCount, list.size());
        for (int i = scrollIndex; i < end; i++) {
            int offset = i - scrollIndex;
            AbstractConfigType<?> type = list.get(i);
            ComponentFactory display = type.getComponentFactory();
            display.addComponents(this, type, 30, 35 + offset * 25, width - 85, 20);
            addComponent(new RemoveCollectionElementComponent(this, this.type, i, width - 50, 35 + offset * 25, 20, 20));
        }
        int posY = 35 + Math.min(end, displayCount) * 25;
        addComponent(new AddCollectionElementComponent<>(this, type, 30, posY, width - 60, 20));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHeader(fontRenderer);
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
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        builder.pos(left, end, 0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        builder.pos(right, end, 0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        builder.pos(right, start, 0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        builder.pos(left, start, 0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
    }

    void renderHeader(FontRenderer renderer) {
        GlStateManager.disableTexture2D();
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
        GlStateManager.enableTexture2D();

        String headerText = TextFormatting.BOLD + title;
        int headerTextWidth = renderer.getStringWidth(headerText);
        renderer.drawStringWithShadow(headerText, (width - headerTextWidth) / 2f, (headerHeight - renderer.FONT_HEIGHT) / 2f, 0xFFFFFF);
    }
}
