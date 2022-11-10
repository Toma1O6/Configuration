package dev.toma.configuration.client.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.config.validate.NotificationSeverity;
import dev.toma.configuration.config.validate.ValidationResult;
import dev.toma.configuration.config.value.ConfigValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigEntryWidget extends ContainerWidget implements WidgetAdder {

    public static final ITextComponent EDIT = new TranslationTextComponent("text.configuration.value.edit");
    public static final ITextComponent BACK = new TranslationTextComponent("text.configuration.value.back");
    public static final ITextComponent REVERT_DEFAULTS = new TranslationTextComponent("text.configuration.value.revert.default");
    public static final ITextComponent REVERT_CHANGES = new TranslationTextComponent("text.configuration.value.revert.changes");

    private final String configId;
    private final ConfigValue<?> value;
    private final int elementIndex;
    private final List<ITextComponent> description;

    private ValidationResult result = ValidationResult.ok();
    private IDescriptionRenderer renderer;
    private boolean lastHoverState;
    private long hoverTimeStart;

    public ConfigEntryWidget(int x, int y, int w, int h, ConfigValue<?> value, String configId) {
        super(x, y, w, h, new TranslationTextComponent("config." + configId + ".option." + value.getId()));
        this.configId = configId;
        this.value = value;
        this.elementIndex = -1;
        this.description = this.getDescription(value);
    }

    public ConfigEntryWidget(int x, int y, int w, int h, ConfigValue<?> value, int i, String configId) {
        super(x, y, w, h, new StringTextComponent("[" + i + "] " + new TranslationTextComponent("config." + configId + ".option." + value.getId()).getString()));
        this.configId = configId;
        this.value = value;
        this.elementIndex = i;
        this.description = this.getDescription(value);
    }

    private List<ITextComponent> getDescription(ConfigValue<?> value) {
        return Arrays.stream(value.getDescription()).map(StringTextComponent::new).collect(Collectors.toList());
    }

    public void setDescriptionRenderer(IDescriptionRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        FontRenderer font = Minecraft.getInstance().font;
        if (!lastHoverState && isHovered) {
            hoverTimeStart = System.currentTimeMillis();
        }
        boolean isError = !this.result.isOk();
        font.draw(stack, this.getMessage(), this.x, this.y + (this.height - font.lineHeight) / 2.0F, 0xFFFFFF);
        super.renderButton(stack, mouseX, mouseY, partialTicks);
        if ((isError || isHovered) && renderer != null) {
            long totalHoverTime = System.currentTimeMillis() - hoverTimeStart;
            if (isError || totalHoverTime >= 750L) {
                NotificationSeverity severity = this.result.getSeverity();
                IFormattableTextComponent textComponent = this.result.getText().withStyle(severity.getExtraFormatting());
                renderer.drawDescription(stack, mouseX, mouseY, this, severity, isError ? Collections.singletonList(textComponent) : this.description);
            }
        }
        this.lastHoverState = isHovered;
    }

    @Override
    public void setValidationResult(ValidationResult result) {
        this.result = result;
    }

    @Override
    public <W extends Widget> W addConfigWidget(ToWidgetFunction<W> function) {
        W widget = function.asWidget(this.x, this.y, this.width, this.height, this.configId);
        return this.addWidget(widget);
    }

    @FunctionalInterface
    public interface IDescriptionRenderer {
        void drawDescription(MatrixStack stack, int mouseX, int mouseY, Widget widget, NotificationSeverity severity, List<ITextComponent> text);
    }
}
