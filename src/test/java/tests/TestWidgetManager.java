package tests;

import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.TypeKey;
import dev.toma.configuration.api.client.IWidgetManager;
import dev.toma.configuration.api.client.IWidgetPlacer;
import dev.toma.configuration.api.client.IWidgetRenderer;
import dev.toma.configuration.api.client.widget.ConfigLayoutWidget;
import dev.toma.configuration.api.client.widget.IWidgetStyle;
import dev.toma.configuration.api.client.widget.LabelWidget;
import dev.toma.configuration.api.client.widget.WidgetType;
import dev.toma.configuration.client.WidgetManager;
import net.minecraft.util.ResourceLocation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestWidgetManager {

    private IWidgetManager manager;

    @Before
    public void startup() {
        manager = new WidgetManager();
    }

    @Test
    public void testPlacerHiearchy() {
        TypeKey parent = TypeKey.newKey(new ResourceLocation("test:parent"), 0);
        TypeKey child = TypeKey.inherit(new ResourceLocation("test:child"), parent);
        IWidgetPlacer placer = this::testPlace;
        manager.setPlacement(parent, placer);
        Assert.assertEquals(placer, manager.getPlacement(parent));
        Assert.assertEquals(placer, manager.getPlacement(child));
    }

    @Test
    public void testRendererOutput() {
        IWidgetRenderer<LabelWidget> labelRenderer = (widget, stack, mc, mouseX, mouseY, partialTicks) -> {};
        manager.setRenderer(WidgetType.LABEL, labelRenderer);
        Assert.assertEquals(labelRenderer, manager.getRenderer(WidgetType.LABEL));
    }

    @Test
    public void testStyleOutput() {
        IWidgetStyle<LabelWidget> labelStyle = widget -> {};
        manager.setStyle(WidgetType.LABEL, labelStyle);
        Assert.assertEquals(labelStyle, manager.getStyle(WidgetType.LABEL));
    }

    private <V> void testPlace(IConfigType<V> type, ConfigLayoutWidget<? extends IConfigType<V>> layout) {}
}
