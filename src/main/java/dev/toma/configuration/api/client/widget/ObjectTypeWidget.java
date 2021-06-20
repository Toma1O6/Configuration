package dev.toma.configuration.api.client.widget;

import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.client.HorizontalAlignment;
import dev.toma.configuration.api.client.VerticalAlignment;
import dev.toma.configuration.api.type.ObjectType;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;

public class ObjectTypeWidget extends ConfigWidget<ObjectType> {

    public int border;
    public HorizontalAlignment horizontalAlignment = HorizontalAlignment.CENTER;
    public VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;

    private ObjectTypeWidget(WidgetType<? extends ObjectTypeWidget> widgetType, ObjectType type, int x, int y, int width, int height) {
        super(widgetType, type, x, y, width, height);
        border = 0xFFFFFFFF;
        background = 0xFF << 24;
        foreground = 0xFFFFFFFF;
    }

    public static ObjectTypeWidget create(WidgetType<? extends ObjectTypeWidget> widgetType, IConfigType<?> type, int x, int y, int width, int height) {
        try {
            return new ObjectTypeWidget(widgetType, (ObjectType) type, x, y, width, height);
        } catch (ClassCastException cce) {
            throw new ReportedException(CrashReport.makeCrashReport(cce, "Object button is applicable only for object config types"));
        }
    }
}
