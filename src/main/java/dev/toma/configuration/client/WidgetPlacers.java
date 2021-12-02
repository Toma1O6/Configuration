package dev.toma.configuration.client;

import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.client.widget.ConfigLayoutWidget;
import dev.toma.configuration.api.client.widget.IColumn;
import dev.toma.configuration.api.client.widget.WidgetType;
import dev.toma.configuration.api.type.DoubleType;
import dev.toma.configuration.api.type.IntType;

@Deprecated
public final class WidgetPlacers {

    public static <V> void noPlacement(IConfigType<V> type, ConfigLayoutWidget<? extends IConfigType<V>> layout) {}

    public static void booleanPlacement(IConfigType<?> t, ConfigLayoutWidget<? extends IConfigType<?>> layout) {
        layout.addColumn(IColumn.relative(0.45, WidgetType.LABEL));
        layout.addColumn(IColumn.relative(0.55, WidgetType.BINARY_BUTTON).setMargin(5));
    }

    public static void intPlacement(IConfigType<?> t, ConfigLayoutWidget<? extends IConfigType<?>> layout) {
        IntType type = (IntType) t;
        switch (type.getDisplayType()) {
            case TEXT_FIELD:
                layout.addColumn(IColumn.relative(0.45, WidgetType.LABEL));
                layout.addColumn(IColumn.relative(0.55, WidgetType.INTEGER_TEXT_FIELD).setMargin(5));
                break;
            case SLIDER:
                layout.addColumn(IColumn.relative(0.45, WidgetType.LABEL));
                layout.addColumn(IColumn.relative(0.55, WidgetType.INT_SLIDER).setMargin(5));
                break;
            case TEXT_FIELD_SLIDER:
                layout.addColumn(IColumn.relative(0.3, WidgetType.LABEL));
                layout.addColumn(IColumn.relative(0.3, WidgetType.INT_SLIDER).setStyle("hideValue"));
                layout.addColumn(IColumn.relative(0.4, WidgetType.INTEGER_TEXT_FIELD).setMargin(2));
                break;
        }
    }

    public static void doublePlacement(IConfigType<?> t, ConfigLayoutWidget<? extends IConfigType<?>> layout) {
        DoubleType type = (DoubleType) t;
        switch (type.getDisplayType()) {
            case TEXT_FIELD:
                layout.addColumn(IColumn.relative(0.45, WidgetType.LABEL));
                layout.addColumn(IColumn.relative(0.55, WidgetType.DOUBLE_TEXT_FIELD).setMargin(5));
                break;
            case SLIDER:
                layout.addColumn(IColumn.relative(0.45, WidgetType.LABEL));
                layout.addColumn(IColumn.relative(0.55, WidgetType.DOUBLE_SLIDER).setMargin(5));
                break;
            case TEXT_FIELD_SLIDER:
                layout.addColumn(IColumn.relative(0.3, WidgetType.LABEL));
                layout.addColumn(IColumn.relative(0.3, WidgetType.DOUBLE_SLIDER).setStyle("hideValue"));
                layout.addColumn(IColumn.relative(0.4, WidgetType.DOUBLE_TEXT_FIELD).setMargin(2));
                break;
        }
    }

    public static void stringPlacement(IConfigType<?> t, ConfigLayoutWidget<? extends IConfigType<?>> layout) {
        layout.addColumn(IColumn.relative(0.45, WidgetType.LABEL));
        layout.addColumn(IColumn.relative(0.55, WidgetType.STRING_TEXT_FIELD).setMargin(5));
    }

    public static void colorPlacement(IConfigType<?> t, ConfigLayoutWidget<? extends IConfigType<?>> layout) {
        layout.addColumn(IColumn.relative(0.45, WidgetType.LABEL));
        layout.addColumn(IColumn.absolute(20, WidgetType.COLOR_DISPLAY));
        layout.addColumn(IColumn.relative(0.55, WidgetType.STRING_TEXT_FIELD).setMargin(2));
    }

    public static void arrayPlacement(IConfigType<?> t, ConfigLayoutWidget<? extends IConfigType<?>> layout) {
        layout.addColumn(IColumn.relative(0.45, WidgetType.LABEL));
        layout.addColumn(IColumn.relative(0.55, WidgetType.ARRAY_BUTTON).setMargin(5));
    }

    public static void collectionPlacement(IConfigType<?> t, ConfigLayoutWidget<? extends IConfigType<?>> layout) {
        layout.addColumn(IColumn.relative(1.0, WidgetType.COLLECTION_BUTTON));
    }

    public static void objectPlacement(IConfigType<?> t, ConfigLayoutWidget<? extends IConfigType<?>> layout) {
        layout.addColumn(IColumn.relative(1.0, WidgetType.OBJECT_BUTTON));
    }
}