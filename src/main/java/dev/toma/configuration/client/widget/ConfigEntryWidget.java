package dev.toma.configuration.client.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.client.WidgetAdder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class ConfigEntryWidget extends ContainerWidget implements WidgetAdder {

    public static final ITextComponent EDIT = new TranslationTextComponent("text.configuration.value.edit");
    public static final ITextComponent BACK = new TranslationTextComponent("text.configuration.value.back");
    public static final ITextComponent REVERT_DEFAULTS = new TranslationTextComponent("text.configuration.value.revert.default");
    public static final ITextComponent REVERT_CHANGES = new TranslationTextComponent("text.configuration.value.revert.changes");

    private final String configId;
    private final ConfigValue<?> value;
    private final int elementIndex;

    private ITextComponent error;

    public ConfigEntryWidget(int x, int y, int w, int h, ConfigValue<?> value, String configId) {
        super(x, y, w, h, new TranslationTextComponent("config." + configId + ".option." + value.getId()));
        this.configId = configId;
        this.value = value;
        this.elementIndex = -1;
    }

    public ConfigEntryWidget(int x, int y, int w, int h, ConfigValue<?> value, int i, String configId) {
        super(x, y, w, h, new StringTextComponent("[" + i + "] " + new TranslationTextComponent("config." + configId + ".option." + value.getId()).getString()));
        this.configId = configId;
        this.value = value;
        this.elementIndex = i;
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        FontRenderer font = Minecraft.getInstance().font;
        if (error != null) {
            font.draw(stack, error, x, y + (height - font.lineHeight) / 2.0F, 0xFF4444);
        } else {
            font.draw(stack, this.getMessage(), this.x, this.y + (this.height - font.lineHeight) / 2.0F, 0xFFFFFF);
        }
        super.renderButton(stack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void setError(@Nullable ITextComponent error) {
        this.error = error;
    }

    @Override
    public void addConfigWidget(ToWidgetFunction function) {
        Widget widget = function.asWidget(this.x, this.y, this.width, this.height, this.configId);
        this.addWidget(widget);
    }

    @Override
    public ConfigValue<?> getConfigValue() {
        return value;
    }

    @Override
    public int elementIndex() {
        return elementIndex;
    }
}
