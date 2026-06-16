package dev.toma.configuration.client.screen;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.client.ConfigurationClient;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.ConfigEntryWidget;
import dev.toma.configuration.client.widget.ThemedButtonWidget;
import dev.toma.configuration.client.widget.render.TextureRenderer;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.io.ConfigurationFileManager;
import dev.toma.configuration.config.validate.ValidationResult;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.ObjectValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.time.Duration;
import java.util.Collection;
import java.util.List;

public abstract class AbstractConfigScreen extends Screen implements ConfigEntryWidget.IValidationRenderer {

    public static final int HEADER_HEIGHT = 35;
    public static final int FOOTER_HEIGHT = 30;
    public static final WidgetSprites BUTTON_SPRITES = new WidgetSprites(
            Identifier.withDefaultNamespace("widget/button"),
            Identifier.withDefaultNamespace("widget/button_disabled"),
            Identifier.withDefaultNamespace("widget/button_highlighted")
    );
    public static final Marker MARKER = MarkerManager.getMarker("Screen");
    public static final Component LABEL_BACK = Component.translatable("text.configuration.value.back");
    public static final Component LABEL_SAVE_AND_CLOSE = Component.translatable("text.configuration.value.save_and_close");
    public static final Identifier ICON_REVERT = Identifier.fromNamespaceAndPath(Configuration.MODID, "textures/icons/revert.png");
    public static final Identifier ICON_REVERT_DEFAULT = Identifier.fromNamespaceAndPath(Configuration.MODID, "textures/icons/revert_default.png");
    public static final Identifier ICON_APPLY = Identifier.fromNamespaceAndPath(Configuration.MODID, "textures/icons/apply.png");
    protected final ConfigHolder<?> holder;
    protected final ConfigTheme theme;
    protected final Screen last;

    protected int index;
    protected int pageSize;

    private ThemedButtonWidget applyButton;
    private ThemedButtonWidget revertButton;
    private ThemedButtonWidget revertDefaultButton;
    private DeferredDescription deferredDescription;

    public AbstractConfigScreen(Component title, Screen previous, ConfigHolder<?> configHolder) {
        super(title);
        this.holder = configHolder;
        this.theme = ConfigurationClient.getConfigTheme(configHolder);
        this.last = previous;
    }

    public String getConfigId() {
        return this.holder.getConfigId();
    }

    @Override
    public void onClose() {
        super.onClose();
        this.saveConfig(true);
    }

    public static void renderScrollbar(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int index, int valueCount, int paging, int bgColor) {
        if (valueCount <= paging)
            return;
        double step = height / (double) valueCount;
        int min = Mth.floor(index * step);
        int max = Mth.ceil((index + paging) * step);
        int y1 = y + min;
        int y2 = y + max;
        graphics.fill(x, y, x + width, y + height, bgColor);

        graphics.fill(x, y1, x + width, y2, 0xFF888888);
        graphics.fill(x, y1, x + width - 1, y2 - 1, 0xFFEEEEEE);
        graphics.fill(x + 1, y1 + 1, x + width - 1, y2 - 1, 0xFFCCCCCC);
    }

    protected void addSettingsButton() {
        ThemedButtonWidget settings = addRenderableWidget(new ThemedButtonWidget(width - 25, 5, 20, 20, Component.literal("..."), theme));
        settings.setBackgroundRenderer(theme.getButtonBackground(settings));
        settings.setClickListener((widget, mouseX, mouseY) -> {
            ConfigSettingsScreen settingsScreen = new ConfigSettingsScreen(this);
            this.minecraft.gui.setScreen(settingsScreen);
        });
        settings.setTooltip(Tooltip.create(Component.translatable("options.title")));
        settings.setTooltipDelay(Duration.ofMillis(300));
    }

    protected void addFooter() {
        int centerY = this.height - FOOTER_HEIGHT + (FOOTER_HEIGHT - 20) / 2;
        Component backLabel = this.isRoot() ? LABEL_SAVE_AND_CLOSE : LABEL_BACK;
        ThemedButtonWidget backButton = addRenderableWidget(new ThemedButtonWidget(5, centerY, 120, 20, backLabel, theme));
        backButton.setBackgroundRenderer(theme.getButtonBackground(backButton));
        backButton.setClickListener((widget, mouseX, mouseY) -> this.buttonBackClicked());

        applyButton = addRenderableWidget(new ThemedButtonWidget(width - 25, centerY, 20, 20, CommonComponents.EMPTY, theme));
        applyButton.setBackgroundRenderer(theme.getButtonBackground(applyButton));
        applyButton.setForegroundRenderer(new TextureRenderer(ICON_APPLY, 2, 2, 16, 16));
        applyButton.setClickListener((widget, mouseX, mouseY) -> {
            if (this.holder.isChanged()) {
                this.holder.save();
                this.init(width, height);
            }
        });
        applyButton.setTooltip(Tooltip.create(ConfigEntryWidget.APPLY));
        applyButton.setTooltipDelay(Duration.ofMillis(300));
        applyButton.active = holder.isChanged();

        revertDefaultButton = addRenderableWidget(new ThemedButtonWidget(width - 50, centerY, 20, 20, CommonComponents.EMPTY, theme));
        revertDefaultButton.setBackgroundRenderer(theme.getButtonBackground(revertDefaultButton));
        revertDefaultButton.setForegroundRenderer(new TextureRenderer(ICON_REVERT_DEFAULT, 2, 2, 16, 16));
        revertDefaultButton.setClickListener((widget, mouseX, mouseY) -> this.buttonRevertToDefaultClicked());
        revertDefaultButton.setTooltip(Tooltip.create(ConfigEntryWidget.REVERT_DEFAULTS));
        revertDefaultButton.setTooltipDelay(Duration.ofMillis(300));
        revertDefaultButton.active = holder.isChangedFromDefault();

        revertButton = addRenderableWidget(new ThemedButtonWidget(width - 75, centerY, 20, 20, CommonComponents.EMPTY, theme));
        revertButton.setBackgroundRenderer(theme.getButtonBackground(revertButton));
        revertButton.setForegroundRenderer(new TextureRenderer(ICON_REVERT, 2, 2, 16, 16));
        revertButton.setClickListener((widget, mouseX, mouseY) -> this.buttonRevertChangesClicked());
        revertButton.setTooltip(Tooltip.create(ConfigEntryWidget.REVERT_CHANGES));
        revertButton.setTooltipDelay(Duration.ofMillis(300));
        revertButton.active = holder.isChanged();
    }

    @Override
    public void tick() {
        if (applyButton != null)
            applyButton.active = holder.isChanged();
        if (revertDefaultButton != null)
            revertDefaultButton.active = holder.isChangedFromDefault();
        if (revertButton != null)
            revertButton.active = holder.isChanged();
    }

    protected void correctScrollingIndex(int count) {
        if (index + pageSize > count) {
            index = Math.max(count - pageSize, 0);
        }
    }

    protected boolean isRoot() {
        return !(last instanceof AbstractConfigScreen);
    }

    private void buttonBackClicked() {
        this.minecraft.gui.setScreen(this.last);
        this.saveConfig();
    }

    private void buttonRevertToDefaultClicked() {
        DialogScreen dialog = new DialogScreen(ConfigEntryWidget.REVERT_DEFAULTS, new Component[] {ConfigEntryWidget.REVERT_DEFAULTS_DIALOG_TEXT}, this);
        dialog.onConfirmed(screen -> {
            Configuration.LOGGER.info(MARKER, "Reverting config {} to default values", this.getConfigId());
            this.revertToDefault(this.holder.values());
            ConfigurationFileManager.saveClientValues(this.holder);
            dialog.displayPreviousScreen();
        });
        minecraft.gui.setScreen(dialog);
    }

    private void buttonRevertChangesClicked() {
        DialogScreen dialog = new DialogScreen(ConfigEntryWidget.REVERT_CHANGES, new Component[] {ConfigEntryWidget.REVERT_CHANGES_DIALOG_TEXT}, this);
        dialog.onConfirmed(screen -> {
            ConfigurationFileManager.reloadClientValues(this.holder);
            dialog.displayPreviousScreen();
        });
        minecraft.gui.setScreen(dialog);
    }

    private void revertToDefault(Collection<ConfigValue<?>> configValues) {
        configValues.forEach(val -> {
            if (val instanceof ObjectValue objVal) {
                this.revertToDefault(objVal.get().values());
            } else if (val.isChangedFromDefault()) {
                val.forceSetDefaultValue();
            }
        });
    }

    private void saveConfig() {
        saveConfig(false);
    }

    private void saveConfig(boolean force) {
        if (force || this.isRoot()) {
            ConfigurationFileManager.saveClientValues(this.holder);
        }
    }

    @Override
    public void drawDescription(GuiGraphicsExtractor graphics, AbstractWidget widget, List<FormattedCharSequence> text, ValidationResult.Type type, int textColor) {
        this.deferredDescription = new DeferredDescription(type, text, textColor, widget.getX() + 5, widget.getY() + widget.getHeight() + 10);
    }

    @Override
    public void drawIcon(GuiGraphicsExtractor graphics, AbstractWidget widget, ValidationResult.Type type) {
        this.renderValidationIcon(type, graphics, widget, widget.getX() - 22, widget.getY() + 1);
    }

    public void renderValidationIcon(ValidationResult.Type type, GuiGraphicsExtractor graphics, AbstractWidget widget, int x, int y) {
        Identifier icon = type.iconPath;
        graphics.blit(RenderPipelines.GUI_TEXTURED, icon, x, y, 0.0F, 0.0F, 16, 16, 16, 16);
    }

    public static boolean canRenderBackground(Minecraft minecraft) {
        return minecraft.level == null || !Configuration.options.getConfigInstance().hideBackground;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float renderDelta) {
        if (canRenderBackground(minecraft)) {
            super.extractBackground(graphics, mouseX, mouseY, renderDelta);
        }
    }

    protected void renderPost(GuiGraphicsExtractor graphics, float renderDelta) {
        if (this.deferredDescription != null) {
            this.deferredDescription.render(graphics, this.font, this.width, this.height, renderDelta);
            this.deferredDescription = null;
        }
    }

    public record DeferredDescription(ValidationResult.Type type, List<FormattedCharSequence> texts, int textColor, int x, int y) {

        public void render(GuiGraphicsExtractor graphics, Font font, int width, int height, float renderDelta) {
            if (!texts.isEmpty()) {
                int maxTextWidth = 0;
                for(FormattedCharSequence textComponent : texts) {
                    int textWidth = font.width(textComponent);
                    if (textWidth > maxTextWidth) {
                        maxTextWidth = textWidth;
                    }
                }
                int startX = x + 12;
                int startY = y - 12;
                int heightOffset = 8;
                if (texts.size() > 1) {
                    heightOffset += 2 + (texts.size() - 1) * 10;
                }

                if (startX + maxTextWidth > width) {
                    startX -= 28 + maxTextWidth;
                }
                if (startY + heightOffset + 6 > height) {
                    startY = height - heightOffset - 6;
                }

                int background = type.backgroundColor;
                int fadeMin = type.backgroundFadeMinColor;
                int fadeMax = type.backgroundFadeMaxColor;
                graphics.fillGradient(startX - 3, startY - 4, startX + maxTextWidth + 3, startY - 3, background, background);
                graphics.fillGradient(startX - 3, startY + heightOffset + 3, startX + maxTextWidth + 3, startY + heightOffset + 4, background, background);
                graphics.fillGradient(startX - 3, startY - 3, startX + maxTextWidth + 3, startY + heightOffset + 3, background, background);
                graphics.fillGradient(startX - 4, startY - 3, startX - 3, startY + heightOffset + 3, background, background);
                graphics.fillGradient(startX + maxTextWidth + 3, startY - 3, startX + maxTextWidth + 4, startY + heightOffset + 3, background, background);
                graphics.fillGradient(startX - 3, startY - 3 + 1, startX - 3 + 1, startY + heightOffset + 3 - 1, fadeMin, fadeMax);
                graphics.fillGradient(startX + maxTextWidth + 2, startY - 3 + 1, startX + maxTextWidth + 3, startY + heightOffset + 3 - 1, fadeMin, fadeMax);
                graphics.fillGradient(startX - 3, startY - 3, startX + maxTextWidth + 3, startY - 3 + 1, fadeMin, fadeMin);
                graphics.fillGradient(startX - 3, startY + heightOffset + 2, startX + maxTextWidth + 3, startY + heightOffset + 3, fadeMax, fadeMax);

                // Draw descriptions in batch, should refactor this too?
                graphics.nextStratum();
                for(int i = 0; i < texts.size(); i++) {
                    FormattedCharSequence textComponent = texts.get(i);
                    graphics.text(font, textComponent, startX, startY, textColor, false);
                    if (i == 0) {
                        startY += 2;
                    }
                    startY += 10;
                }
            }
        }
    }
}
