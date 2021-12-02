package dev.toma.configuration.client;

import dev.toma.configuration.api.TypeKey;
import dev.toma.configuration.api.client.IWidgetManager;
import dev.toma.configuration.api.client.IWidgetPlacer;
import dev.toma.configuration.api.client.IWidgetRenderer;
import dev.toma.configuration.api.client.widget.IWidgetStyle;
import dev.toma.configuration.api.client.widget.Widget;
import dev.toma.configuration.api.client.widget.WidgetType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Deprecated
@SuppressWarnings("unchecked")
public class WidgetManager implements IWidgetManager {

    public static final IWidgetPlacer NO_PLACEMENT = WidgetPlacers::noPlacement;
    public static final IWidgetStyle<?> NO_STYLE = widget -> {};
    private final Map<TypeKey, IWidgetPlacer> widgetControlMap;
    private final Map<WidgetType<?>, IWidgetRenderer<?>> rendererMap;
    private final Map<WidgetType<?>, IStyleContainer<?>> styleContainerMap;

    public WidgetManager() {
        widgetControlMap = new HashMap<>();
        initPlacers();
        rendererMap = new HashMap<>();
        initRenderers();
        styleContainerMap = new HashMap<>();
    }

    @Override
    public IWidgetPlacer getPlacement(TypeKey key) {
        IWidgetPlacer placer = widgetControlMap.get(key);
        if (placer == null) {
            if (key.isChild()) {
                placer = getPlacement(key.getParent());
            }
            else placer = NO_PLACEMENT;
        }
        return placer;
    }

    @Override
    public void setPlacement(TypeKey key, IWidgetPlacer control) {
        if (control == null)
            control = NO_PLACEMENT;
        widgetControlMap.put(Objects.requireNonNull(key), control);
    }

    @Override
    public <W extends Widget> void setRenderer(WidgetType<W> type, IWidgetRenderer<W> renderer) {
        rendererMap.put(type, renderer);
    }

    @Override
    public <W extends Widget> IWidgetRenderer<W> getRenderer(WidgetType<W> type) {
        return (IWidgetRenderer<W>) rendererMap.get(type);
    }

    @Override
    public <W extends Widget> void setStyle(WidgetType<W> type, IWidgetStyle<W> style, String key) {
        IStyleContainer<W> container = (IStyleContainer<W>) styleContainerMap.computeIfAbsent(type, k -> new StyleContainer<>());
        container.registerStyle(key, style);
    }

    @Override
    public <W extends Widget> IWidgetStyle<W> getStyle(WidgetType<W> type, String key) {
        IStyleContainer<W> container = (IStyleContainer<W>) styleContainerMap.get(type);
        if (container == null)
            return noStyle();
        IWidgetStyle<W> style = container.getStyle(key);
        return style != null ? style : noStyle();
    }

    private void initPlacers() {
        setPlacement(TypeKey.BOOLEAN, WidgetPlacers::booleanPlacement);
        setPlacement(TypeKey.INT, WidgetPlacers::intPlacement);
        setPlacement(TypeKey.DOUBLE, WidgetPlacers::doublePlacement);
        setPlacement(TypeKey.STRING, WidgetPlacers::stringPlacement);
        setPlacement(TypeKey.COLOR, WidgetPlacers::colorPlacement);
        setPlacement(TypeKey.ARRAY, WidgetPlacers::arrayPlacement);
        setPlacement(TypeKey.COLLECTION, WidgetPlacers::collectionPlacement);
        setPlacement(TypeKey.OBJECT, WidgetPlacers::objectPlacement);
    }

    private void initRenderers() {
        setRenderer(WidgetType.LABEL, WidgetRenderers::renderLabel);
        setRenderer(WidgetType.BUTTON, WidgetRenderers::renderButton);
        setRenderer(WidgetType.BINARY_BUTTON, WidgetRenderers::renderBinaryButton);
        setRenderer(WidgetType.ARRAY_BUTTON, WidgetRenderers::renderArrayButton);
        setRenderer(WidgetType.COLLECTION_BUTTON, WidgetRenderers::renderCollectionButton);
        setRenderer(WidgetType.OBJECT_BUTTON, WidgetRenderers::renderObjectButton);
        setRenderer(WidgetType.STRING_TEXT_FIELD, WidgetRenderers::renderTextField);
        setRenderer(WidgetType.INTEGER_TEXT_FIELD, WidgetRenderers::renderTextField);
        setRenderer(WidgetType.DOUBLE_TEXT_FIELD, WidgetRenderers::renderTextField);
        setRenderer(WidgetType.INT_SLIDER, WidgetRenderers::renderSlider);
        setRenderer(WidgetType.DOUBLE_SLIDER, WidgetRenderers::renderSlider);
        setRenderer(WidgetType.COLOR_DISPLAY, WidgetRenderers::renderColorDisplay);
    }

    private static <W extends Widget> IWidgetStyle<W> noStyle() {
        return (IWidgetStyle<W>) NO_STYLE;
    }
}
