package dev.toma.configuration.api.client;

import dev.toma.configuration.api.client.widget.IWidgetStyle;
import dev.toma.configuration.api.client.widget.Widget;

public interface IStyleContainer<W extends Widget> {

    void registerStyle(String styleKey, IWidgetStyle<W> style);

    IWidgetStyle<W> getStyle(String styleKey);
}
