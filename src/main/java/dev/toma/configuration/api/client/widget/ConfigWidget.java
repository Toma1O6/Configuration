package dev.toma.configuration.api.client.widget;

import dev.toma.configuration.api.IConfigType;

public abstract class ConfigWidget<T extends IConfigType<?>> extends Widget {

    public ConfigWidget(WidgetType<?> widgetType, T t, int x, int y, int width, int height) {
        super(widgetType, t, x, y, width, height);
    }

    @SuppressWarnings("unchecked")
    public T getConfigType() {
        return (T) configType;
    }
}
