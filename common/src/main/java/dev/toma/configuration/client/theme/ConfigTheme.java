package dev.toma.configuration.client.theme;

import dev.toma.configuration.client.theme.adapter.DisplayAdapter;
import dev.toma.configuration.client.theme.adapter.DisplayAdapterManager;
import dev.toma.configuration.client.widget.AbstractThemeWidget;
import dev.toma.configuration.client.widget.ColorWidget;
import dev.toma.configuration.client.widget.EditBoxWidget;
import dev.toma.configuration.client.widget.SliderWidget;
import dev.toma.configuration.client.widget.render.IRenderer;
import dev.toma.configuration.config.adapter.AdapterHolder;
import dev.toma.configuration.config.adapter.TypeMatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.HashSet;
import java.util.Set;
import java.util.function.UnaryOperator;

public final class ConfigTheme {

    // widgets
    private final Set<AdapterHolder<DisplayAdapter>> displayAdapters = new HashSet<>();

    private Header header;
    private Footer footer;
    private Scrollbar scrollbar;
    private ConfigEntry configEntry;

    private Integer backgroundFillColor;
    private int widgetTextColor;
    private int widgetTextColorHovered;
    private int widgetTextColorDisabled;

    private BackgroundRendererFactory<AbstractThemeWidget> buttonBackground = BackgroundRendererFactory.none();
    private BackgroundRendererFactory<EditBoxWidget> editBoxBackground = BackgroundRendererFactory.none();
    private BackgroundRendererFactory<SliderWidget<?>> sliderBackground = BackgroundRendererFactory.none();
    private BackgroundRendererFactory<SliderWidget<?>> sliderHandle = BackgroundRendererFactory.none();
    private BackgroundRendererFactory<ColorWidget> colorBackground = BackgroundRendererFactory.none();

    public ConfigTheme copy() {
        ConfigTheme theme = new ConfigTheme();
        theme.displayAdapters.addAll(displayAdapters);
        theme.header = header;
        theme.footer = footer;
        theme.scrollbar = scrollbar;
        theme.configEntry = configEntry;
        theme.backgroundFillColor = backgroundFillColor;
        theme.widgetTextColor = widgetTextColor;
        theme.widgetTextColorHovered = widgetTextColorHovered;
        theme.widgetTextColorDisabled = widgetTextColorDisabled;
        theme.buttonBackground = buttonBackground;
        theme.editBoxBackground = editBoxBackground;
        theme.sliderBackground = sliderBackground;
        theme.sliderHandle = sliderHandle;
        theme.colorBackground = colorBackground;
        return theme;
    }

    public DisplayAdapter getAdapter(Class<?> type) {
        Class<?> mappedType = DisplayAdapterManager.mapType(type);
        return this.displayAdapters.stream()
                .filter(holder -> holder.test(mappedType))
                .sorted()
                .findFirst()
                .map(AdapterHolder::adapter)
                .orElse(null);
    }

    public void registerDisplayAdapter(TypeMatcher matcher, DisplayAdapter adapter) {
        AdapterHolder<DisplayAdapter> adapterHolder = new AdapterHolder<>(matcher, adapter);
        this.displayAdapters.remove(adapterHolder); // clear existing value when updating
        this.displayAdapters.add(adapterHolder);
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public void setFooter(Footer footer) {
        this.footer = footer;
    }

    public void setScrollbar(Scrollbar scrollbar) {
        this.scrollbar = scrollbar;
    }

    public void setConfigEntry(ConfigEntry configEntry) {
        this.configEntry = configEntry;
    }

    public void setBackgroundFillColor(Integer backgroundFillColor) {
        this.backgroundFillColor = backgroundFillColor;
    }

    public void setWidgetTextColor(int widgetTextColor, int widgetTextColorHovered, int widgetTextColorDisabled) {
        this.widgetTextColor = widgetTextColor;
        this.widgetTextColorHovered = widgetTextColorHovered;
        this.widgetTextColorDisabled = widgetTextColorDisabled;
    }

    public void setButtonBackground(BackgroundRendererFactory<AbstractThemeWidget> buttonBackground) {
        this.buttonBackground = buttonBackground;
    }

    public void setEditBoxBackground(BackgroundRendererFactory<EditBoxWidget> editBoxBackground) {
        this.editBoxBackground = editBoxBackground;
    }

    public void setSliderBackground(BackgroundRendererFactory<SliderWidget<?>> sliderBackground) {
        this.sliderBackground = sliderBackground;
    }

    public void setSliderHandle(BackgroundRendererFactory<SliderWidget<?>> sliderHandle) {
        this.sliderHandle = sliderHandle;
    }

    public void setColorBackground(BackgroundRendererFactory<ColorWidget> colorBackground) {
        this.colorBackground = colorBackground;
    }

    // Getters

    public Header getHeader() {
        return header;
    }

    public Footer getFooter() {
        return footer;
    }

    public Scrollbar getScrollbar() {
        return scrollbar;
    }

    public ConfigEntry getConfigEntry() {
        return configEntry;
    }

    public Integer getBackgroundFillColor() {
        return backgroundFillColor;
    }

    public int getWidgetTextColor(boolean active, boolean hovered) {
        return active ? hovered ? widgetTextColorHovered : widgetTextColor : widgetTextColorDisabled;
    }

    public IRenderer getButtonBackground(AbstractThemeWidget widget) {
        return buttonBackground.create(widget);
    }

    public IRenderer getEditBoxBackground(EditBoxWidget widget) {
        return editBoxBackground.create(widget);
    }

    public IRenderer getSliderBackground(SliderWidget<?> widget) {
        return sliderBackground.create(widget);
    }

    public IRenderer getSliderHandle(SliderWidget<?> widget) {
        return sliderHandle.create(widget);
    }

    public IRenderer getColorBackground(ColorWidget widget) {
        return colorBackground.create(widget);
    }

    public record Header(Component customText, int backgroundColor, int foregroundColor) {}

    public record Footer(int backgroundColor) {}

    public record Scrollbar(int width, Integer backgroundColor) {}

    public record ConfigEntry(int color, UnaryOperator<Style> modifiedValueStyle, Integer hoveredColorBackground) {}

    @FunctionalInterface
    public interface BackgroundRendererFactory<T> {

        IRenderer create(T type);

        static <T> BackgroundRendererFactory<T> none() {
            return t -> null;
        }
    }
}
