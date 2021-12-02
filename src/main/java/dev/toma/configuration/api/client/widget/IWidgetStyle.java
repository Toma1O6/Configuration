package dev.toma.configuration.api.client.widget;

/**
 * Widget style. Basically a Consumer with Widget type parameter.
 * Since every widget exposes bunch of public properties such as background or foreground color,
 * this is the best place to set your desired values
 * @param <W> Type of widget
 */
@Deprecated
public interface IWidgetStyle<W extends Widget> {
    /**
     * Applies style on supplied widget
     * @param widget Widget for style application
     */
    void styleWidget(W widget);
}
