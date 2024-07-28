package dev.toma.configuration.client.screen;

import dev.toma.configuration.client.widget.ConfigEntryWidget;
import dev.toma.configuration.config.ConfigHolder;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

import static dev.toma.configuration.client.screen.AbstractConfigScreen.FOOTER_HEIGHT;
import static dev.toma.configuration.client.screen.AbstractConfigScreen.HEADER_HEIGHT;

public class ConfigGroupScreen extends Screen {

    protected final Screen last;
    protected final String groupId;
    protected final List<ConfigHolder<?>> configHolders;
    protected int index;
    protected int pageSize;

    public ConfigGroupScreen(Screen last, String groupId, List<ConfigHolder<?>> configHolders) {
        super(Component.translatable("text.configuration.screen.select_config"));
        this.last = last;
        this.groupId = groupId;
        this.configHolders = configHolders;
    }

    @Override
    protected void init() {
        final int viewportMin = HEADER_HEIGHT;
        final int viewportHeight = this.height - viewportMin - FOOTER_HEIGHT;
        this.pageSize = (viewportHeight - 20) / 25;
        this.correctScrollingIndex(this.configHolders.size());
        int errorOffset = (viewportHeight - 20) - (this.pageSize * 25 - 5);
        int offset = 0;
        int posX = 30;
        int componentWidth = this.width - 2 * posX;
        for (int i = this.index; i < this.index + this.pageSize; i++) {
            int j = i - this.index;
            if (i >= configHolders.size())
                break;
            int correct = errorOffset / (this.pageSize - j);
            errorOffset -= correct;
            offset += correct;
            ConfigHolder<?> value = configHolders.get(i);
            int y = viewportMin + 10 + j * 25 + offset;
            String configId = value.getConfigId();
            this.addRenderableWidget(new LeftAlignedLabel(posX, y, componentWidth, 20, Component.translatable("config.screen." + configId), this.font));
            this.addRenderableWidget(Button.builder(ConfigEntryWidget.OPEN, btn -> {
                ConfigScreen screen = new ConfigScreen(value, value.getTitle(), value.getValueMap(), this);
                minecraft.setScreen(screen);
            }).pos(getValueX(posX, componentWidth), y).size(getValueWidth(componentWidth), 20).build());
        }
        initFooter();
    }

    static int getValueX(int x, int width) {
        return x + width - getValueWidth(width);
    }

    static int getValueWidth(int width) {
        return width / 3;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics, mouseX, mouseY, partialTicks);
        int titleWidth = this.font.width(this.title);
        graphics.drawString(font, this.title, (this.width - titleWidth) / 2, (HEADER_HEIGHT - this.font.lineHeight) / 2, 0xFFFFFF);
        graphics.fill(0, 0, width, HEADER_HEIGHT, 0x99 << 24);
        graphics.fill(0, height - FOOTER_HEIGHT, width, height, 0x99 << 24);
        graphics.fill(0, HEADER_HEIGHT, width, height - FOOTER_HEIGHT, 0x55 << 24);
        AbstractConfigScreen.renderScrollbar(graphics, width - 5, HEADER_HEIGHT, 5, height - FOOTER_HEIGHT - HEADER_HEIGHT, index, configHolders.size(), pageSize, 0xFF << 24);
        renderables.forEach(renderable -> renderable.render(graphics, mouseX, mouseY, partialTicks));
    }

    protected void initFooter() {
        int centerY = this.height - FOOTER_HEIGHT + (FOOTER_HEIGHT - 20) / 2;
        addRenderableWidget(Button.builder(AbstractConfigScreen.LABEL_BACK, btn -> minecraft.setScreen(last)).pos(5, centerY).size(120, 20).build());
    }

    protected void correctScrollingIndex(int count) {
        if (index + pageSize > count) {
            index = Math.max(count - pageSize, 0);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amountX, double amountY) {
        int scale = (int) -amountY;
        int next = this.index + scale;
        if (next >= 0 && next + this.pageSize <= this.configHolders.size()) {
            this.index = next;
            this.init(minecraft, width, height);
            return true;
        }
        return false;
    }

    protected static final class LeftAlignedLabel extends AbstractWidget {

        private final Font font;

        public LeftAlignedLabel(int x, int y, int width, int height, Component label, Font font) {
            super(x, y, width, height, label);
            this.font = font;
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
            graphics.drawString(font, this.getMessage(), this.getX(), this.getY() + (this.height - this.font.lineHeight) / 2, 0xAAAAAA);
        }

        @Override
        protected boolean isValidClickButton(int p_230987_1_) {
            return false;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
        }
    }
}
