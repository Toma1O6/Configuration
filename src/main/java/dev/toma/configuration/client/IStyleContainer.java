package dev.toma.configuration.client;

import dev.toma.configuration.api.client.widget.IWidgetStyle;
import dev.toma.configuration.api.client.widget.Widget;

@Deprecated
public interface IStyleContainer<W extends Widget> {
    void registerStyle(String styleKey, IWidgetStyle<W> style);
    IWidgetStyle<W> getStyle(String styleKey);
}
