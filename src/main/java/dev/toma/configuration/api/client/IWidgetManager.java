package dev.toma.configuration.api.client;

import dev.toma.configuration.api.TypeKey;
import dev.toma.configuration.api.client.widget.IWidgetStyle;
import dev.toma.configuration.api.client.widget.Widget;
import dev.toma.configuration.api.client.widget.WidgetType;

/**
 * Provides complete control over widget rendering, placing and styling
 */
@Deprecated
public interface IWidgetManager {

    /**
     * Set widget placer for specifig config type key
     * @param key Key used to match config types
     * @param placer WidgetPlacer implementation
     */
    void setPlacement(TypeKey key, IWidgetPlacer placer);

    /**
     * Set widget renderer for specific WidgetType
     * @param type WidgetType - acts as key
     * @param renderer Widget renderer
     * @param <W> Type of widget
     */
    <W extends Widget> void setRenderer(WidgetType<W> type, IWidgetRenderer<W> renderer);

    /**
     * Set widget style for specific WidgetType
     * @param type WidgetType - key
     * @param style Style which should be applied to widget
     * @param key Style ID in case you want to have multiple styles for each widget type
     * @param <W> Type of widget
     */
    <W extends Widget> void setStyle(WidgetType<W> type, IWidgetStyle<W> style, String key);

    // USED INTERNALLY
    IWidgetPlacer getPlacement(TypeKey key);
    <W extends Widget> IWidgetRenderer<W> getRenderer(WidgetType<W> type);
    <W extends Widget> IWidgetStyle<W> getStyle(WidgetType<W> type, String key);
}
