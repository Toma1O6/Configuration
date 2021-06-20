package dev.toma.configuration.api.client.widget;

import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.client.HorizontalAlignment;
import dev.toma.configuration.api.client.VerticalAlignment;
import dev.toma.configuration.api.type.CollectionType;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;

public class CollectionButton<T extends IConfigType<?>> extends ConfigWidget<CollectionType<T>> {

    public int borderColor;
    public HorizontalAlignment horizontalAlignment = HorizontalAlignment.CENTER;
    public VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;

    private CollectionButton(WidgetType<? extends CollectionButton<?>> widgetType, CollectionType<T> type, int x, int y, int width, int height) {
        super(widgetType, type, x, y, width, height);
        borderColor = 0xFFFFFFFF;
        background = 0xFF << 24;
        foreground = 0xFFFFFFFF;
    }

    public static <T extends IConfigType<?>> CollectionButton<?> create(WidgetType<? extends CollectionButton<?>> widgetType, IConfigType<?> type, int x, int y, int width, int height) {
        try {
            return new CollectionButton<>(widgetType, (CollectionType<T>) type, x, y, width, height);
        } catch (ClassCastException cce) {
            throw new ReportedException(CrashReport.makeCrashReport(cce, "Collection button is applicable only for collection config types"));
        }
    }
}
