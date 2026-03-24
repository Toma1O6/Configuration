package dev.toma.configuration.client.widget;

import dev.toma.configuration.client.WidgetAdder;
import dev.toma.configuration.client.screen.AbstractConfigScreen;
import dev.toma.configuration.client.screen.WidgetPlacerHelper;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.config.validate.ValidationResult;
import dev.toma.configuration.config.value.ConfigValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;
import java.util.function.UnaryOperator;

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

    private ValidationResult result = ValidationResult.success();
    private IValidationRenderer renderer;
    private boolean lastHoverState;
    private long hoverTimeStart;

    public ConfigEntryWidget(int x, int y, int w, int h, ConfigValue<?> value, String configId, ConfigTheme theme) {
        this(x, y, w, h, value.getValueData().getTitle(), value, configId, theme);
    }

    public ConfigEntryWidget(int x, int y, int w, int h, Component label, ConfigValue<?> value, String configId, ConfigTheme theme) {
        super(x, y, w, h, label);
        this.configValue = value;
        this.configId = configId;
        this.description = value.getDescription();
        this.theme = theme;
    }

    public void setDescriptionRenderer(IValidationRenderer renderer) {
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
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        ConfigTheme.ConfigEntry configEntry = this.theme.getConfigEntry();
        if (isHovered) {
            if (!lastHoverState) {
                hoverTimeStart = System.currentTimeMillis();
            }
            if (configEntry.hoveredColorBackground() != null) {
                graphics.fill(this.getX() - 30, this.getY() - 2, this.getRight() + 30, this.getBottom() + 2, configEntry.hoveredColorBackground());
            }
        }
        ValidationResult validationResult = this.getValidationResult();
        boolean isError = this.hasGuiError();
        MutableComponent label = Component.literal(this.getMessage().getString()).withStyle(this.getMessage().getStyle());
        UnaryOperator<Style> modifiedStyle = configEntry.modifiedValueStyle();
        if (this.configValue.isChanged() && modifiedStyle != null) {
            label.withStyle(modifiedStyle.apply(label.getStyle()));
        }
        int entryLeft = WidgetPlacerHelper.getLeft(this.getX(), this.width);
        boolean backgroundRenderMode = AbstractConfigScreen.canRenderBackground(minecraft);
        if (backgroundRenderMode || isHovered) {
            renderScrollingString(label, entryLeft - 5, backgroundRenderMode ? configEntry.color() : 0xFFFFFFFF, graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
        }
        super.extractWidgetRenderState(graphics, mouseX, mouseY, partialTicks);
        ValidationResult.Type type = validationResult.type();
        boolean validationRendering = false;
        if (type.isWarningOrError()) {
            validationRendering = true;
            this.renderer.drawIcon(graphics, this, type);
        }
        if ((isError || isHovered) && renderer != null) {
            long totalHoverTime = System.currentTimeMillis() - hoverTimeStart;
            if (isError || totalHoverTime >= 750L) {
                List<Component> messages = validationRendering ? validationResult.description() : this.description;
                List<FormattedCharSequence> lines = messages.stream()
                        .flatMap(text -> font.split(text, this.width / 2).stream())
                        .toList();
                boolean hasDescription = lines.size() > 1 || (lines.size() == 1 && !lines.getFirst().equals(CommonComponents.EMPTY));
                if (hasDescription) {
                    this.renderer.drawDescription(graphics, this, lines, type, type.textColor);
                }
            }
        }
        this.lastHoverState = isHovered;
    }

    public void renderScrollingString(Component text, int right, int color, ActiveTextCollector textCollector) {
        int left = this.getX();
        int top = this.getY();
        int bottom = this.getBottom();
        Component coloredText = text.copy().withStyle(style -> style.withColor(color));
        textCollector.acceptScrolling(coloredText, left, left, right, top, bottom, textCollector.defaultParameters());
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

    private ValidationResult getValidationResult() {
        ValidationResult valueResult = this.configValue.getValidationResult() != null ? this.configValue.getValidationResult() : ValidationResult.success();
        return valueResult.type().isMoreSevereThan(this.result.type()) ? valueResult : this.result;
    }

    private boolean hasGuiError() {
        ValidationResult result = this.getValidationResult();
        return !result.type().isValid();
    }


    public interface IValidationRenderer {

        void drawIcon(GuiGraphicsExtractor graphics, AbstractWidget widget, ValidationResult.Type type);

        void drawDescription(GuiGraphicsExtractor graphics, AbstractWidget widget, List<FormattedCharSequence> text, ValidationResult.Type type, int textColor);
    }
}
