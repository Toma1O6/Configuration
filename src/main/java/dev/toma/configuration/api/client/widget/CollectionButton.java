package dev.toma.configuration.api.client.widget;

import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.ModConfig;
import dev.toma.configuration.api.client.HorizontalAlignment;
import dev.toma.configuration.api.client.IClientSettings;
import dev.toma.configuration.api.client.ScreenOpenContext;
import dev.toma.configuration.api.client.VerticalAlignment;
import dev.toma.configuration.api.client.screen.WidgetScreen;
import dev.toma.configuration.api.type.CollectionType;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;

public class CollectionButton<T extends IConfigType<?>> extends ConfigWidget<CollectionType<T>> {

    public int borderColor;
    public HorizontalAlignment horizontalAlignment = HorizontalAlignment.CENTER;
    public VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;
    private WidgetScreen<?> parent;

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
            throw new ReportedException(CrashReport.forThrowable(cce, "Collection button is applicable only for collection config types"));
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
                WidgetScreen<?> screen = settings.getConfigCollectionScreenFactory().createScreen(parent, getConfigType(), ctx);
                Minecraft.getInstance().setScreen(screen);
                return true;
            }
        }
        return false;
    }
}
