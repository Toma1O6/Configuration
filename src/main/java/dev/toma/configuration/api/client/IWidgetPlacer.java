package dev.toma.configuration.api.client;

import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.client.widget.ConfigLayoutWidget;

public interface IWidgetPlacer {

    <V> void place(IConfigType<V> type, ConfigLayoutWidget<? extends IConfigType<V>> container);
}
