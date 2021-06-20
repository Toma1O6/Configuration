package dev.toma.configuration.api.client.widget;

import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.client.HorizontalAlignment;
import dev.toma.configuration.api.client.VerticalAlignment;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class LabelWidget extends Widget {

    public ITextComponent content;
    public VerticalAlignment verticalAlignment;
    public HorizontalAlignment horizontalAlignment;

    public LabelWidget(WidgetType<? extends LabelWidget> widgetType, IConfigType<?> type, int x, int y, int width, int height) {
        super(widgetType, type, x, y, width, height);

        content = type != null ? new StringTextComponent(type.getId()) : StringTextComponent.EMPTY;
        verticalAlignment = VerticalAlignment.CENTER;
        horizontalAlignment = HorizontalAlignment.LEFT;
        foreground = 0xFFFFFFFF;
        background = 0;
    }
}
