package dev.toma.configuration.api.client.widget;

import dev.toma.configuration.api.IConfigType;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;

public class TwoStateButton extends ConfigWidget<IConfigType<Boolean>> {

    public int borderColor;

    private TwoStateButton(WidgetType<? extends TwoStateButton> widgetType, IConfigType<Boolean> type, int x, int y, int width, int height) {
        super(widgetType, type, x, y, width, height);
        updateForegroundColor();
        borderColor = 0xFFFFFFFF;
        background = 0xFF << 24;
    }

    @SuppressWarnings("unchecked")
    public static TwoStateButton create(WidgetType<? extends TwoStateButton> widgetType, IConfigType<?> type, int x, int y, int width, int height) {
        try {
            return new TwoStateButton(widgetType, (IConfigType<Boolean>) type, x, y, width, height);
        } catch (ClassCastException cce) {
            throw new ReportedException(CrashReport.forThrowable(cce, "Two state button is applicable only for boolean config types"));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (isMouseOver(mouseX, mouseY) && mouseButton == 0) {
            if (!visibilityState.isDisabled()) {
                IConfigType<Boolean> type = getConfigType();
                type.set(!type.get());
                updateForegroundColor();
                playPressSound();
                return true;
            }
        }
        return false;
    }

    public String getContent() {
        return String.valueOf(configType.get());
    }

    private void updateForegroundColor() {
        foreground = getConfigType().get() ? 0xFF00AA << 8 : 0xFFAA << 16;
    }
}
