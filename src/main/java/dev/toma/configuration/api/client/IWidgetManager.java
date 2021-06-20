package dev.toma.configuration.api.client;

import dev.toma.configuration.api.TypeKey;
import dev.toma.configuration.api.client.widget.IWidgetStyle;
import dev.toma.configuration.api.client.widget.Widget;
import dev.toma.configuration.api.client.widget.WidgetType;

public interface IWidgetManager {

    IWidgetPlacer getPlacement(TypeKey key);

    void setPlacement(TypeKey key, IWidgetPlacer placer);

    <W extends Widget> void setRenderer(WidgetType<W> type, IWidgetRenderer<W> renderer);

    <W extends Widget> IWidgetRenderer<W> getRenderer(WidgetType<W> type);

    <W extends Widget> void setStyle(WidgetType<W> type, IWidgetStyle<W> style);

    <W extends Widget> IWidgetStyle<W> getStyle(WidgetType<W> type);
}
