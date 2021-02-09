package dev.toma.configuration.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.api.ConfigPlugin;
import dev.toma.configuration.api.client.BackgroundRenderer;
import dev.toma.configuration.api.type.AbstractConfigType;
import dev.toma.configuration.api.type.CollectionType;
import dev.toma.configuration.client.ComponentFactory;
import dev.toma.configuration.client.screen.component.AddCollectionElementComponent;
import dev.toma.configuration.client.screen.component.Component;
import dev.toma.configuration.client.screen.component.RemoveCollectionElementComponent;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class CollectionScreen<T extends AbstractConfigType<?>> extends ComponentScreen {

    final Screen screen;
    final CollectionType<T> type;
    final BackgroundRenderer renderer;
    int displayCount;
    int scrollIndex;

    public CollectionScreen(Screen parentScreen, CollectionType<T> type, String modid) {
        super(new StringTextComponent(type.getId() != null ? type.getId() : "Unnamed collection"), modid);
        this.screen = parentScreen;
        this.type = type;
        Optional<ConfigPlugin> optional = Configuration.getPlugin(modid);
        if(optional.isPresent() && optional.get().getBackgroundRenderer() != null) {
            this.renderer = optional.get().getBackgroundRenderer();
        } else this.renderer = BackgroundRenderer.DirtBackground.INSTANCE;
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
    public void closeScreen() {
        super.closeScreen();
        minecraft.displayGuiScreen(screen);
    }

    @Override
    public int getTextColor() {
        return renderer.getTextColor();
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
            AbstractConfigType<?> type = list.get(i);
            ComponentFactory display = type.getDisplayFactory();
            display.addComponents(this, type, 30, 35 + offset * 25, width - 85, 20);
            addComponent(new RemoveCollectionElementComponent(this, this.type, i, width - 50, 35 + offset * 25, 20, 20));
        }
        int posY = 35 + Math.min(end, displayCount) * 25;
        addComponent(new AddCollectionElementComponent<>(this, type, 30, posY, width - 60, 20));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderer.drawBackground(this, matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHeader(matrixStack, font);
        int count = type.get().size();
        if(count > displayCount) {
            this.renderScrollbar(matrixStack, count);
        }
        this.renderHoveredInfo(matrixStack, mouseX, mouseY);
    }

    void renderScrollbar(MatrixStack stack, int count) {
        int height = ((1 + displayCount) * 25) - 5;
        double step = height / (double) count;
        double start = 35 + scrollIndex * step;
        double end = 35 + (scrollIndex + displayCount) * step;
        int left = width - 20;
        int right = width - 10;
        Component.drawColorShape(stack, left, 35, right, 35 + height, 0.0F, 0.0F, 0.0F, 1.0F);
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

    void renderHeader(MatrixStack stack, FontRenderer renderer) {
        Matrix4f matrix4f = stack.getLast().getMatrix();
        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        int headerHeight = 20;
        float headerAlpha = 0.4F;
        BufferBuilder builder = Tessellator.getInstance().getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        builder.pos(matrix4f, 0, headerHeight, 0).color(0.0F, 0.0F, 0.0F, headerAlpha).endVertex();
        builder.pos(matrix4f, width, headerHeight, 0).color(0.0F, 0.0F, 0.0F, headerAlpha).endVertex();
        builder.pos(matrix4f, width, 0, 0).color(0.0F, 0.0F, 0.0F, headerAlpha).endVertex();
        builder.pos(matrix4f, 0, 0, 0).color(0.0F, 0.0F, 0.0F, headerAlpha).endVertex();
        builder.finishDrawing();
        WorldVertexBufferUploader.draw(builder);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture();

        String headerText = TextFormatting.BOLD + title.getUnformattedComponentText();
        int headerTextWidth = renderer.getStringWidth(headerText);
        renderer.drawStringWithShadow(stack, headerText, (width - headerTextWidth) / 2f, (headerHeight - renderer.FONT_HEIGHT) / 2f, 0xFFFFFF);
    }
}
