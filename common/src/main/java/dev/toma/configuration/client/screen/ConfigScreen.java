package dev.toma.configuration.client.screen;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.theme.adapter.DisplayAdapter;
import dev.toma.configuration.client.widget.ConfigEntryWidget;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.value.ConfigValue;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.message.FormattedMessage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigScreen extends AbstractConfigScreen {

    private final Map<String, ConfigValue<?>> valueMap;

    public ConfigScreen(ConfigHolder<?> configHolder, Component screenTitle, Map<String, ConfigValue<?>> valueMap, Screen previous) {
        super(screenTitle, previous, configHolder);
        this.valueMap = valueMap;
    }

    @Override
    protected void init() {
        final int viewportMin = HEADER_HEIGHT;
        final int viewportHeight = this.height - viewportMin - FOOTER_HEIGHT;
        this.pageSize = (viewportHeight - 20) / 25;
        this.correctScrollingIndex(this.valueMap.size());
        List<ConfigValue<?>> values = new ArrayList<>(this.valueMap.values());
        int errorOffset = (viewportHeight - 20) - (this.pageSize * 25 - 5);
        int offset = 0;
        for (int i = this.index; i < this.index + this.pageSize; i++) {
            int j = i - this.index;
            if (i >= values.size())
                break;
            int correct = errorOffset / (this.pageSize - j);
            errorOffset -= correct;
            offset += correct;
            ConfigValue<?> value = values.get(i);
            ConfigEntryWidget widget = addRenderableWidget(new ConfigEntryWidget(30, viewportMin + 10 + j * 25 + offset, this.width - 60, 20, value, this.getConfigId(), this.theme));
            widget.setDescriptionRenderer(this);
            TypeAdapter.AdapterContext context = value.getSerializationContext();
            Field field = context.getOwner();
            DisplayAdapter adapter = this.theme.getAdapter(field.getType());
            if (adapter == null) {
                Configuration.LOGGER.error(MARKER, "Missing display adapter for {} type, will not be displayed in GUI", field.getType().getSimpleName());
                continue;
            }
            try {
                adapter.placeWidgets(this.holder, value, field, this.theme, widget);
            } catch (ClassCastException e) {
                Configuration.LOGGER.error(MARKER, new FormattedMessage("Unable to create config field for {}", field.getType().getSimpleName()), e);
            }
        }
        this.addFooter();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics, mouseX, mouseY, partialTicks);
        // HEADER
        ConfigTheme.Header themeHeader = this.theme.getHeader();
        ConfigTheme.Footer footer = this.theme.getFooter();
        Component headerLabel = themeHeader.customText() != null ? themeHeader.customText() : this.title;
        int titleWidth = this.font.width(headerLabel);
        graphics.drawString(font, headerLabel, (this.width - titleWidth) / 2, (HEADER_HEIGHT - this.font.lineHeight) / 2, themeHeader.foregroundColor(), true);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.fill(0, 0, width, HEADER_HEIGHT, themeHeader.backgroundColor());
        graphics.fill(0, height - FOOTER_HEIGHT, width, height, footer.backgroundColor());
        Integer fillColor = this.theme.getBackgroundFillColor();
        if (fillColor != null) {
            graphics.fill(0, HEADER_HEIGHT, width, height - FOOTER_HEIGHT, fillColor);
        }
        renderables.forEach(renderable -> renderable.render(graphics, mouseX, mouseY, partialTicks));
        ConfigTheme.Scrollbar scrollbar = this.theme.getScrollbar();
        renderScrollbar(graphics, width - scrollbar.width(), HEADER_HEIGHT, scrollbar.width(), height - FOOTER_HEIGHT - HEADER_HEIGHT, index, valueMap.size(), pageSize, scrollbar.backgroundColor());
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amountX, double amountY) {
        int scale = (int) -amountY;
        int next = this.index + scale;
        if (next >= 0 && next + this.pageSize <= this.valueMap.size()) {
            this.index = next;
            this.init(minecraft, width, height);
            return true;
        }
        return false;
    }
}
