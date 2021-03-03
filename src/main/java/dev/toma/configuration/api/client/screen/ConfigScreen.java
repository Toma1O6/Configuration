package dev.toma.configuration.api.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.api.client.ClientHandles;
import dev.toma.configuration.api.client.ComponentFactory;
import dev.toma.configuration.api.client.IModID;
import dev.toma.configuration.api.client.component.Component;
import dev.toma.configuration.api.type.AbstractConfigType;
import dev.toma.configuration.api.type.ObjectType;
import dev.toma.configuration.internal.FileTracker;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ConfigScreen extends ComponentScreen {

    final Screen screen;
    final ObjectType type;
    int displayCount;
    int scrollIndex;

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public ConfigScreen(Screen screen, ObjectType type, String modid, ClientHandles handles) {
        super(new StringTextComponent(type.getId() != null ? type.getId() : Configuration.getPlugin(modid).get().getConfigFileName()), modid, handles);
        this.screen = screen;
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
        if(!(screen instanceof IModID)) {
            FileTracker.INSTANCE.scheduleConfigUpdate(this.getModID(), FileTracker.UpdateAction.WRITE);
        }
    }

    @Override
    protected void init() {
        displayCount = (height - 40) / 25;
        Map<String, AbstractConfigType<?>> map = type.get();
        List<AbstractConfigType<?>> list = new ArrayList<>(map.values());
        list.sort(Comparator.comparingInt(AbstractConfigType::getSortIndex));
        int end = Math.min(scrollIndex + displayCount, list.size());
        for (int i = scrollIndex; i < end; i++) {
            int offset = i - scrollIndex;
            AbstractConfigType<?> type = list.get(i);
            ComponentFactory factory = type.getComponentFactory();
            factory.addComponents(this, type, 30, 35 + offset * 25, width - 60, 20);
        }
    }

    @Override
    public void renderScreenPost(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderHeader(font);
        int count = type.get().size();
        if(count > displayCount) {
            this.renderScrollbar(count);
        }
        this.renderHoveredInfo(mouseX, mouseY);
    }

    void renderScrollbar(int count) {
        int height = (displayCount * 25) - 5;
        double step = height / (double) count;
        double start = 35 + scrollIndex * step;
        double end = 35 + (scrollIndex + displayCount) * step;
        int left = width - 20;
        int right = width - 10;
        Component.drawColorShape(left, 35, right, 35 + height, 0.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.disableTexture();
        BufferBuilder builder = Tessellator.getInstance().getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        builder.pos(left, end, 0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        builder.pos(right, end, 0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        builder.pos(right, start, 0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        builder.pos(left, start, 0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        builder.finishDrawing();
        WorldVertexBufferUploader.draw(builder);
        GlStateManager.enableTexture();
    }

    void renderHeader(FontRenderer renderer) {
        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        int headerHeight = 20;
        float headerAlpha = 0.4F;
        BufferBuilder builder = Tessellator.getInstance().getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        builder.pos(0, headerHeight, 0).color(0.0F, 0.0F, 0.0F, headerAlpha).endVertex();
        builder.pos(width, headerHeight, 0).color(0.0F, 0.0F, 0.0F, headerAlpha).endVertex();
        builder.pos(width, 0, 0).color(0.0F, 0.0F, 0.0F, headerAlpha).endVertex();
        builder.pos(0, 0, 0).color(0.0F, 0.0F, 0.0F, headerAlpha).endVertex();
        builder.finishDrawing();
        WorldVertexBufferUploader.draw(builder);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture();

        String headerText = TextFormatting.BOLD + title.getUnformattedComponentText();
        int headerTextWidth = renderer.getStringWidth(headerText);
        renderer.drawStringWithShadow(headerText, (width - headerTextWidth) / 2f, (headerHeight - renderer.FONT_HEIGHT) / 2f, 0xFFFFFF);
    }
}
