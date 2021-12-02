package dev.toma.configuration.api.client;

import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.client.widget.ConfigLayoutWidget;
import dev.toma.configuration.api.client.widget.IColumn;

/**
 * Widget placer handles placement into container. Defines what type of widget is placed and where.
 * Refer to {@link dev.toma.configuration.client.WidgetPlacers} for examples
 */
@Deprecated
public interface IWidgetPlacer {

    /**
     * Creates placement layout for specific config type
     * @param type Config type being placed into container
     * @param container Container which contains all widgets for supplied config type. Take a look at {@link ConfigLayoutWidget#addColumn(IColumn)} method
     * @param <V> Data type of config type implementation
     * @see IColumn
     */
    <V> void place(IConfigType<V> type, ConfigLayoutWidget<? extends IConfigType<V>> container);
}
