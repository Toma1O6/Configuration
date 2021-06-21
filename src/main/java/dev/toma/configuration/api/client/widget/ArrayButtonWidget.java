package dev.toma.configuration.api.client.widget;

import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.client.HorizontalAlignment;
import dev.toma.configuration.api.client.VerticalAlignment;
import dev.toma.configuration.api.type.ArrayType;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;

public class ArrayButtonWidget extends ConfigWidget<ArrayType<?>> {

    public int border;
    public HorizontalAlignment horizontalAlignment = HorizontalAlignment.CENTER;
    public VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;

    private ArrayButtonWidget(WidgetType<? extends ArrayButtonWidget> widgetType, ArrayType<?> type, int x, int y, int width, int height) {
        super(widgetType, type, x, y, width, height);
        border = 0xFFFFFFFF;
        background = 0xFF << 24;
        foreground = 0xFFFFFFFF;
    }

    public static ArrayButtonWidget create(WidgetType<? extends ArrayButtonWidget> widgetType, IConfigType<?> type, int x, int y, int width, int height) {
        try {
            return new ArrayButtonWidget(widgetType, (ArrayType<?>) type, x, y, width, height);
        } catch (ClassCastException cce) {
            throw new ReportedException(CrashReport.forThrowable(cce, "Array button is applicable only for array config types"));
        }
    }
}
