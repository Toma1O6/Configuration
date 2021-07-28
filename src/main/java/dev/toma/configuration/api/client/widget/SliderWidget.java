package dev.toma.configuration.api.client.widget;

import dev.toma.configuration.api.IBounded;
import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.IFormatted;
import dev.toma.configuration.api.type.DoubleType;
import dev.toma.configuration.api.type.IntType;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.util.Mth;

import java.util.function.Consumer;
import java.util.function.Function;

public class SliderWidget<N extends Number, T extends IConfigType<N> & IBounded<N>> extends ConfigWidget<T> {

    public int unsetBorderColor;
    public int setBorderColor;
    public boolean dragging;
    public boolean showValue = true;
    public Function<String, String> formatter = Function.identity();

    public float sliderValue;

    private final Consumer<N> valueExternalChangeCallback;
    private final Function<Double, N> mapper;

    protected SliderWidget(WidgetType<? extends SliderWidget<?, ?>> widgetType, T type, int x, int y, int width, int height, Function<Double, N> mapper) {
        super(widgetType, type, x, y, width, height);
        this.mapper = mapper;
        valueExternalChangeCallback = this::updateSliderValue;
        getConfigType().addListener(valueExternalChangeCallback);
        updateSliderValue(getConfigType().get());

        if (type instanceof IFormatted) {
            IFormatted formatted = (IFormatted) type;
            formatter = formatted::format;
        }

        unsetBorderColor = 0xFF343434;
        setBorderColor = 0xFFFFFFFF;
        background = 0xFF << 24;
        foreground = 0xFFFFFFFF;
    }

    protected static <N extends Number, T extends IConfigType<N> & IBounded<N>> SliderWidget<N, T> create(WidgetType<? extends SliderWidget<?, ?>> widgetType, IConfigType<?> type, int x, int y, int width, int height, Function<Double, N> mapper) {
        try {
            return new SliderWidget<>(widgetType, (T) type, x, y, width, height, mapper);
        } catch (ClassCastException cce) {
            throw new ReportedException(CrashReport.forThrowable(cce, "Slider is applicable only for numeric bounded config types"));
        }
    }

    public static SliderWidget<Integer, IntType> createIntSlider(WidgetType<? extends SliderWidget<?, ?>> widgetType, IConfigType<?> type, int x, int y, int width, int height) {
        return create(widgetType, type, x, y, width, height, d -> (int) Math.round(d));
    }

    public static SliderWidget<Double, DoubleType> createDoubleSlider(WidgetType<? extends SliderWidget<?, ?>> widgetType, IConfigType<?> type, int x, int y, int width, int height) {
        return create(widgetType, type, x, y, width, height, Function.identity());
    }

    @Override
    public void save() {
        getConfigType().removeListener(valueExternalChangeCallback);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (!visibilityState.isDisabled() && isMouseOver(mouseX, mouseY) && mouseButton == 0) {
            dragging = true;
            setSliderToMouse(mouseX);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragging) {
            setSliderToMouse(mouseX);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragging) {
            dragging = false;
            return true;
        }
        return false;
    }

    private void updateSliderValue(N number) {
        T configType = getConfigType();
        float min = configType.getMin().floatValue();
        float max = configType.getMax().floatValue();
        float actual = number.floatValue();
        setSliderValue((actual - min) / (max - min));
    }

    private void setSliderToMouse(double mouseX) {
        float target = (int) (mouseX - x);
        if (width >= target) {
            setSliderValue(target / width);
            updateConfigValue();
        }
    }

    private void updateConfigValue() {
        double min = getConfigType().getMin().doubleValue();
        double max = getConfigType().getMax().doubleValue();
        double value = Mth.clamp(min + (max - min) * sliderValue, min, max);
        getConfigType().set(mapper.apply(value));
    }

    private void setSliderValue(float value) {
        sliderValue = Mth.clamp(value, 0.0F, 1.0F);
    }
}
