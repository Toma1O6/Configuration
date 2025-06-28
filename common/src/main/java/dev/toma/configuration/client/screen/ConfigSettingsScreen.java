package dev.toma.configuration.client.screen;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.ConfigurationOptions;
import dev.toma.configuration.config.io.ConfigIO;
import dev.toma.configuration.config.value.BooleanValue;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.time.Duration;

public final class ConfigSettingsScreen extends Screen {

    private static final Component TITLE = Component.translatable("options.title");
    private final Screen parent;

    public ConfigSettingsScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    @Override
    public void onClose() {
        super.onClose();
        this.close();
    }

    @Override
    protected void init() {
        ConfigurationOptions options = Configuration.options.getConfigInstance();
        // Advanced mode
        Checkbox advancedMode = this.addRenderableWidget(
                Checkbox.builder(Component.translatable("text.configuration.options.advanced_mode"), this.font)
                        .pos(10, 40)
                        .maxWidth(this.width - 20)
                        .selected(options.advancedMode)
                        .onValueChange((checkbox1, b) -> Configuration.options.getConfigValue("advancedMode", Boolean.class).ifPresent(value -> ((BooleanValue) value).forceSetValue(b)))
                        .tooltip(Tooltip.create(Component.translatable("text.configuration.options.advanced_mode.tooltip")))
                        .build()
        );
        advancedMode.setTooltipDelay(Duration.ofMillis(500));

        // Hide background
        Checkbox hideBackground = this.addRenderableWidget(
                Checkbox.builder(Component.translatable("text.configuration.options.hide_background"), this.font)
                        .pos(10, 60)
                        .maxWidth(this.width - 20)
                        .selected(options.hideBackground)
                        .onValueChange((checkbox1, b) -> Configuration.options.getConfigValue("hideBackground", Boolean.class).ifPresent(value -> ((BooleanValue) value).forceSetValue(b)))
                        .tooltip(Tooltip.create(Component.translatable("text.configuration.options.hide_background.tooltip")))
                        .build()
        );
        hideBackground.setTooltipDelay(Duration.ofMillis(500));

        // Back button
        this.addRenderableWidget(
                Button.builder(Component.translatable("gui.back"), button -> close())
                        .pos(5, this.height - 25)
                        .size(120, 20)
                        .build()
        );
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float renderDelta) {
        super.renderBackground(graphics, mouseX, mouseY, renderDelta);
        // Header BG
        graphics.fill(0, 0, this.width, 30, 0x99 << 24);
        // Footer BG
        graphics.fill(0, this.height - 30, this.width, this.height, 0x99 << 24);
        // Header text
        graphics.drawString(this.font, TITLE, (this.width - this.font.width(TITLE)) / 2, (30 - this.font.lineHeight) / 2, 0xFFFFFFFF);
    }

    private void close() {
        ConfigIO.saveClientValues(Configuration.options);
        this.minecraft.setScreen(this.parent);
    }
}
