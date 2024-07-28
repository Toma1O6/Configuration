package dev.toma.configuration.client.widget;

import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.screen.WidgetPlacerHelper;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.config.validate.NotificationSeverity;
import dev.toma.configuration.config.validate.ValidationResult;
import dev.toma.configuration.config.value.ConfigValue;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class ConfigEntryWidget extends ContainerWidget implements WidgetAdder {

    public static final Component OPEN = Component.translatable("text.configuration.value.open");
    public static final Component APPLY = Component.translatable("text.configuration.value.apply");
    public static final Component REVERT_DEFAULTS = Component.translatable("text.configuration.value.revert.default");
    public static final Component REVERT_DEFAULTS_DIALOG_TEXT = Component.translatable("text.configuration.value.revert.default.dialog");
    public static final Component REVERT_CHANGES = Component.translatable("text.configuration.value.revert.changes");
    public static final Component REVERT_CHANGES_DIALOG_TEXT = Component.translatable("text.configuration.value.revert.changes.dialog");

    private final String configId;
    private final ConfigValue<?> configValue;
    private final List<Component> description;
    private final ConfigTheme theme;

    private ValidationResult result = ValidationResult.ok();
    private IDescriptionRenderer renderer;
    private boolean lastHoverState;
    private long hoverTimeStart;

    public ConfigEntryWidget(int x, int y, int w, int h, ConfigValue<?> value, String configId, ConfigTheme theme) {
        this(x, y, w, h, value.getValueData().getTitle(), value, configId, theme);
    }

    public ConfigEntryWidget(int x, int y, int w, int h, Component label, ConfigValue<?> value, String configId, ConfigTheme theme) {
        super(x, y, w, h, label);
        this.configValue = value;
        this.configId = configId;
        // TODO apply correct config styles for comments
        this.description = value.getValueData().getDescription().stream()
                .map(text -> Component.literal(text.getString()).withStyle(ChatFormatting.GRAY))
                .collect(Collectors.toList());
        this.theme = theme;
    }

    public void setDescriptionRenderer(IDescriptionRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public Component getComponentName() {
        return this.getMessage();
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput p_169152_) {
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        ConfigTheme.ConfigEntry configEntry = this.theme.getConfigEntry();
        if (isHovered) {
            if (!lastHoverState) {
                hoverTimeStart = System.currentTimeMillis();
            }
            if (configEntry.hoveredColorBackground() != null) {
                graphics.fill(this.getX() - 30, this.getY() - 2, this.getRight() + 30, this.getBottom() + 2, configEntry.hoveredColorBackground());
            }
        }
        boolean isError = !this.result.isOk();
        MutableComponent label = Component.literal(this.getMessage().getString()).withStyle(this.getMessage().getStyle());
        UnaryOperator<Style> modifiedStyle = configEntry.modifiedValueStyle();
        if (this.configValue.isChanged() && modifiedStyle != null) {
            label.withStyle(modifiedStyle.apply(label.getStyle()));
        }
        int entryLeft = WidgetPlacerHelper.getLeft(this.getX(), this.width);
        drawScrollingString(graphics, font, label, this.getX(), entryLeft - 5, this.getY() + (this.height - font.lineHeight) / 2, configEntry.color());
        super.renderWidget(graphics, mouseX, mouseY, partialTicks);
        if ((isError || isHovered) && renderer != null) {
            long totalHoverTime = System.currentTimeMillis() - hoverTimeStart;
            if (isError || totalHoverTime >= 750L) {
                NotificationSeverity severity = this.result.severity();
                MutableComponent textComponent = this.result.text().withStyle(severity.getExtraFormatting());
                List<Component> desc = isError ? Collections.singletonList(textComponent) : this.description;
                List<FormattedCharSequence> split = desc.stream().flatMap(text -> font.split(text, this.width / 2).stream()).collect(Collectors.toList());
                renderer.drawDescription(graphics, this, severity, split);
            }
        }
        this.lastHoverState = isHovered;
    }

    @Override
    public void setValidationResult(ValidationResult result) {
        this.result = result;
    }

    @Override
    public <W extends AbstractWidget> W addConfigWidget(boolean editableCheck, ToWidgetFunction<W> function) {
        W widget = function.asWidget(this.getX(), this.getY(), this.width, this.height, this.configId);
        if (editableCheck)
            widget.active = this.configValue.isEditable();
        return this.addRenderableWidget(widget);
    }

    public static void drawScrollingString(GuiGraphics graphics, Font font, Component text, int x1, int x2, int y, int color) {
        int maxWidth = x2 - x1;
        int width = font.width(text);
        if (width <= maxWidth) {
            graphics.drawString(font, text, x1, y, color);
        } else {
            AbstractWidget.renderScrollingString(graphics, font, text, x1, y, x2, y + font.lineHeight, color);
        }
    }

    @FunctionalInterface
    public interface IDescriptionRenderer {
        void drawDescription(GuiGraphics graphics, AbstractWidget widget, NotificationSeverity severity, List<FormattedCharSequence> text);
    }
}
