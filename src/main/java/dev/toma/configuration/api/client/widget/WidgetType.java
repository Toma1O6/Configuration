package dev.toma.configuration.api.client.widget;

import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.client.IClientSettings;
import dev.toma.configuration.api.client.IWidgetManager;
import dev.toma.configuration.api.type.DoubleType;
import dev.toma.configuration.api.type.IntType;

import javax.annotation.Nullable;

public final class WidgetType<W extends Widget> {

    public static final WidgetType<LabelWidget> LABEL = new WidgetType<>("configuration.label", LabelWidget::new);
    public static final WidgetType<ButtonWidget> BUTTON = new WidgetType<>("configuration.button", ButtonWidget::new);
    public static final WidgetType<TwoStateButton> BINARY_BUTTON = new WidgetType<>("configuration.button.binary", TwoStateButton::create);
    public static final WidgetType<ArrayButtonWidget> ARRAY_BUTTON = new WidgetType<>("configuration.button.array", ArrayButtonWidget::create);
    public static final WidgetType<CollectionButton<?>> COLLECTION_BUTTON = new WidgetType<>("configuration.button.collection", CollectionButton::create);
    public static final WidgetType<ObjectTypeWidget> OBJECT_BUTTON = new WidgetType<>("configuration.button.object", ObjectTypeWidget::create);
    public static final WidgetType<InputWidget.StringInput> STRING_TEXT_FIELD = new WidgetType<>("configuration.input.string", InputWidget.StringInput::create);
    public static final WidgetType<InputWidget.IntegerInput> INTEGER_TEXT_FIELD = new WidgetType<>("configuration.input.int", InputWidget.IntegerInput::create);
    public static final WidgetType<InputWidget.DoubleInput> DOUBLE_TEXT_FIELD = new WidgetType<>("configuration.input.double", InputWidget.DoubleInput::create);
    public static final WidgetType<SliderWidget<Integer, IntType>> INT_SLIDER = new WidgetType<>("configuration.slider.int", SliderWidget::createIntSlider);
    public static final WidgetType<SliderWidget<Double, DoubleType>> DOUBLE_SLIDER = new WidgetType<>("configuration.slider.double", SliderWidget::createDoubleSlider);
    public static final WidgetType<ColorDisplayWidget> COLOR_DISPLAY = new WidgetType<>("configuration.color", ColorDisplayWidget::create);

    private final String key;
    private final IWidgetFactory<W> factory;

    public WidgetType(String key, IWidgetFactory<W> factory) {
        this.key = key;
        this.factory = factory;
    }

    public W instantiateWidget(IConfigType<?> type, IClientSettings settings, int x, int y, int width, int height, String styleID) {
        W w = factory.instantiateWidget(this, type, x, y, width, height);
        IWidgetManager manager = settings.getWidgetManager();
        IWidgetStyle<W> style = manager.getStyle(this, styleID);
        style.styleWidget(w);
        return w;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WidgetType<?> that = (WidgetType<?>) o;
        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return "WidgetType{" +
                "key='" + key + '\'' +
                '}';
    }

    public interface IWidgetFactory<W extends Widget> {
        W instantiateWidget(WidgetType<W> widgetType, @Nullable IConfigType<?> configType, int x, int y, int width, int height);
    }
}
