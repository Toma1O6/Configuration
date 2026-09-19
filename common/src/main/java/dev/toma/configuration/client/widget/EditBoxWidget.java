package dev.toma.configuration.client.widget;

import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.widget.render.IRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.StringDecomposer;
import org.jspecify.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class EditBoxWidget extends EditBox implements ThemeWidget {

    public static final WidgetSprites SPRITES = new WidgetSprites(Identifier.withDefaultNamespace("widget/text_field"), Identifier.withDefaultNamespace("widget/text_field_highlighted"));
    protected final ConfigTheme theme;
    protected IRenderer backgroundRenderer;
    protected ChangeListener<EditBoxWidget> changeListener;
    private Predicate<String> filter;

    public EditBoxWidget(Font font, int x, int y, int width, int height, ConfigTheme theme) {
        super(font, x, y, width, height, CommonComponents.EMPTY);
        this.theme = theme;
        this.filter = Objects::nonNull;
    }

    @Override
    public void insertText(String input) {
        if (this.filter == null || this.filter.test(input)) {
            super.insertText(input);
        }
    }

    @Override
    public void setBackgroundRenderer(IRenderer backgroundRenderer) {
        this.backgroundRenderer = backgroundRenderer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends AbstractWidget & ThemeWidget> void setChangeListener(ChangeListener<T> listener) {
        this.changeListener = (ChangeListener<EditBoxWidget>) listener;
    }

    @Override
    public void setChanged() {
        if (this.changeListener != null) {
            this.changeListener.onChanged(this);
        }
    }

    @Override
    public ConfigTheme getTheme() {
        return theme;
    }

    // TODO implement?
    public void setFilter(Predicate<String> filter) {
        this.filter = filter;
    }

    public record NumberFormatter(DecimalFormat format, Supplier<Number> value) implements TextFormatter {

        @Override
        public @Nullable FormattedCharSequence format(String text, int offset) {
            String value = this.format.format(this.value.get());
            return (output) -> StringDecomposer.iterate(value, Style.EMPTY, output);
        }
    }
}
