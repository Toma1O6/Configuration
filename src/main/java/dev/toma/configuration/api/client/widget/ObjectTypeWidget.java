package dev.toma.configuration.api.client.widget;

import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.ModConfig;
import dev.toma.configuration.api.client.HorizontalAlignment;
import dev.toma.configuration.api.client.IClientSettings;
import dev.toma.configuration.api.client.ScreenOpenContext;
import dev.toma.configuration.api.client.VerticalAlignment;
import dev.toma.configuration.api.client.screen.WidgetScreen;
import dev.toma.configuration.api.type.ObjectType;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;

@Deprecated
public class ObjectTypeWidget extends ConfigWidget<ObjectType> {

    public int border;
    public HorizontalAlignment horizontalAlignment = HorizontalAlignment.CENTER;
    public VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;
    private WidgetScreen<?> parent;

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
            throw new ReportedException(CrashReport.forThrowable(cce, "Object button is applicable only for object config types"));
        }
    }

    @Override
    public void assignParent(WidgetScreen<?> screen) {
        this.parent = screen;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (!visibilityState.isDisabled()) {
            if (isMouseOver(mouseX, mouseY) && mouseButton == 0) {
                ScreenOpenContext ctx = parent.getOpeningContext();
                ModConfig config = ctx.getModConfig();
                IClientSettings settings = config.settings();
                playPressSound();
                WidgetScreen<?> screen = settings.getConfigScreenFactory().createScreen(parent, getConfigType(), ctx);
                Minecraft.getInstance().setScreen(screen);
                return true;
            }
        }
        return false;
    }
}
