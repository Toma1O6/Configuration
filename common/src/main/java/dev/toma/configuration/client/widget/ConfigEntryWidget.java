package dev.toma.configuration.client.widget;

import dev.toma.configuration.client.WidgetAdder;
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
import net.minecraft.util.FormattedCharSequence;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigEntryWidget extends ContainerWidget implements WidgetAdder {

    public static final Component OPEN = Component.translatable("text.configuration.value.open");
    public static final Component BACK = Component.translatable("text.configuration.value.back");
    public static final Component REVERT_DEFAULTS = Component.translatable("text.configuration.value.revert.default");
    public static final Component REVERT_DEFAULTS_DIALOG_TEXT = Component.translatable("text.configuration.value.revert.default.dialog");
    public static final Component REVERT_CHANGES = Component.translatable("text.configuration.value.revert.changes");
    public static final Component REVERT_CHANGES_DIALOG_TEXT = Component.translatable("text.configuration.value.revert.changes.dialog");

    private final String configId;
    private final ConfigValue<?> configValue;
    private final List<Component> description;

    private ValidationResult result = ValidationResult.ok();
    private IDescriptionRenderer renderer;
    private boolean lastHoverState;
    private long hoverTimeStart;

    public ConfigEntryWidget(int x, int y, int w, int h, ConfigValue<?> value, String configId) {
        super(x, y, w, h, value.getValueData().getTitle());
        this.configValue = value;
        this.configId = configId;
        // TODO apply correct config styles for comments
        this.description = value.getValueData().getDescription().stream()
                .map(text -> Component.literal(text.getString()).withStyle(ChatFormatting.GRAY))
                .collect(Collectors.toList());
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
        if (!lastHoverState && isHovered) {
            hoverTimeStart = System.currentTimeMillis();
        }
        boolean isError = !this.result.isOk();
        MutableComponent label = Component.literal(this.getMessage().getString()).withStyle(this.getMessage().getStyle());
        if (this.configValue.isChanged()) {
            label.withStyle(ChatFormatting.ITALIC);
        }
        graphics.drawString(font, label, this.getX(), this.getY() + (this.height - font.lineHeight) / 2, 0xFFFFFF);
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
    public <W extends AbstractWidget> W addConfigWidget(ToWidgetFunction<W> function) {
        W widget = function.asWidget(this.getX(), this.getY(), this.width, this.height, this.configId);
        widget.active = this.configValue.isEditable();
        return this.addRenderableWidget(widget);
    }

    @FunctionalInterface
    public interface IDescriptionRenderer {
        void drawDescription(GuiGraphics graphics, AbstractWidget widget, NotificationSeverity severity, List<FormattedCharSequence> text);
    }
}
