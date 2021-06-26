package dev.toma.configuration.client;

import dev.toma.configuration.api.client.widget.IWidgetStyle;
import dev.toma.configuration.api.client.widget.Widget;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StyleContainer<W extends Widget> implements IStyleContainer<W> {

    private final Map<String, IWidgetStyle<W>> styleMap = new HashMap<>();

    @Override
    public void registerStyle(String styleKey, IWidgetStyle<W> style) {
        styleMap.put(styleKey, Objects.requireNonNull(style));
    }

    @Override
    public IWidgetStyle<W> getStyle(String styleKey) {
        return styleMap.get(styleKey);
    }
}
