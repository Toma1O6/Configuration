package dev.toma.configuration.client.theme;

import dev.toma.configuration.client.theme.adapter.DisplayAdapter;
import dev.toma.configuration.client.theme.adapter.DisplayAdapterManager;
import dev.toma.configuration.config.adapter.AdapterHolder;
import dev.toma.configuration.config.adapter.TypeMatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class ConfigTheme {

    // widgets
    private final Set<AdapterHolder<DisplayAdapter>> displayAdapters = new HashSet<>();

    private ResourceLocation backgroundTexture;
    private Header header;
    private Footer footer;
    private Scrollbar scrollbar;

    private Integer backgroundFillColor;

    public ConfigTheme copy() {
        ConfigTheme theme = new ConfigTheme();
        theme.displayAdapters.addAll(displayAdapters);
        theme.backgroundTexture = backgroundTexture;
        theme.header = header;
        theme.footer = footer;
        theme.scrollbar = scrollbar;
        theme.backgroundFillColor = backgroundFillColor;
        return theme;
    }

    public final DisplayAdapter getAdapter(Class<?> type) {
        Class<?> mappedType = DisplayAdapterManager.mapType(type);
        return this.displayAdapters.stream()
                .filter(holder -> holder.test(mappedType))
                .sorted()
                .findFirst()
                .map(AdapterHolder::adapter)
                .orElse(null);
    }

    public final void registerDisplayAdapter(TypeMatcher matcher, DisplayAdapter adapter) {
        this.displayAdapters.add(new AdapterHolder<>(matcher, adapter));
    }

    public void setBackgroundTexture(ResourceLocation backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
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

    public void setBackgroundFillColor(Integer backgroundFillColor) {
        this.backgroundFillColor = backgroundFillColor;
    }

    // Getters
    public ResourceLocation getBackgroundTexture() {
        return this.backgroundTexture;
    }

    public Header getHeader() {
        return header;
    }

    public Footer getFooter() {
        return footer;
    }

    public Scrollbar getScrollbar() {
        return scrollbar;
    }

    public record Header(Component customText, int height, int backgroundColor, int foregroundColor) {}

    public record Footer(int height, int backgroundColor) {} // TODO widget factory

    public record Scrollbar(boolean alwaysRendered, int width, Integer backgroundColor) {}
}
