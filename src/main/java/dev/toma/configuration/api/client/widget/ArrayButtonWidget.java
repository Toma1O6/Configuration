package dev.toma.configuration.api.client.widget;

import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.client.HorizontalAlignment;
import dev.toma.configuration.api.client.VerticalAlignment;
import dev.toma.configuration.api.type.ArrayType;
import dev.toma.configuration.util.ArrayUtils;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;

@SuppressWarnings("unchecked")
public class ArrayButtonWidget extends ConfigWidget<ArrayType<?>> {

    public int border;
    public HorizontalAlignment horizontalAlignment = HorizontalAlignment.CENTER;
    public VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;
    public int selectedIndex;

    private ArrayButtonWidget(WidgetType<? extends ArrayButtonWidget> widgetType, ArrayType<?> type, int x, int y, int width, int height) {
        super(widgetType, type, x, y, width, height);
        border = 0xFFFFFFFF;
        background = 0xFF << 24;
        foreground = 0xFFFFFFFF;

        Object obj = type.get();
        Object[] values = type.collect();
        int index = Mth.clamp(ArrayUtils.indexOf_ref(obj, values), 0, values.length);
        safetySet(index);
    }

    public static ArrayButtonWidget create(WidgetType<? extends ArrayButtonWidget> widgetType, IConfigType<?> type, int x, int y, int width, int height) {
        try {
            return new ArrayButtonWidget(widgetType, (ArrayType<?>) type, x, y, width, height);
        } catch (ClassCastException cce) {
            throw new ReportedException(CrashReport.forThrowable(cce, "Array button is applicable only for array config types"));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (!visibilityState.isDisabled() && isMouseOver(mouseX, mouseY) && mouseButton == 0) {
            offsetSelection(Screen.hasShiftDown() ? -1 : 1);
            playPressSound();
            return true;
        }
        return false;
    }

    private <A> void offsetSelection(int value) {
        ArrayType<A> type = (ArrayType<A>) getConfigType();
        A[] array = type.collect();
        int newIndex = selectedIndex + value;
        if (newIndex >= array.length)
            newIndex = 0;
        else if (newIndex < 0)
            newIndex = array.length - 1;
        selectedIndex = newIndex;
        type.set(array[selectedIndex]);
    }

    // makes sure that contained value is actually from the value array
    private <A> void safetySet(int index) {
        ArrayType<A> type = (ArrayType<A>) getConfigType();
        A[] values = type.collect();
        type.set(values[index]);
        selectedIndex = index;
    }
}
