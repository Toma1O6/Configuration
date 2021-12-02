package dev.toma.configuration.api.client.widget;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.type.ColorType;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.resources.ResourceLocation;

@Deprecated
public class ColorDisplayWidget extends ConfigWidget<ColorType> {

    public static final ResourceLocation DEFAULT_COLOR_BACKGROUND = new ResourceLocation(Configuration.MODID, "textures/background_empty.png");

    public ResourceLocation backgroundTexture = DEFAULT_COLOR_BACKGROUND;

    private ColorDisplayWidget(WidgetType<? extends ColorDisplayWidget> widgetType, ColorType colorType, int x, int y, int width, int height) {
        super(widgetType, colorType, x, y, width, height);
        background = 0xFFFFFFFF;
    }

    public static ColorDisplayWidget create(WidgetType<? extends ColorDisplayWidget> widgetType, IConfigType<?> colorType, int x, int y, int width, int height) {
        try {
            return new ColorDisplayWidget(widgetType, (ColorType) colorType, x, y, width, height);
        } catch (ClassCastException cce) {
            throw new ReportedException(CrashReport.forThrowable(cce, "Color display is applicable only for color config types"));
        }
    }
}
