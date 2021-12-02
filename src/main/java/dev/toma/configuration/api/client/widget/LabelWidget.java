package dev.toma.configuration.api.client.widget;

import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.client.HorizontalAlignment;
import dev.toma.configuration.api.client.VerticalAlignment;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

@Deprecated
public class LabelWidget extends Widget {

    public Component content;
    public VerticalAlignment verticalAlignment;
    public HorizontalAlignment horizontalAlignment;

    public LabelWidget(WidgetType<? extends LabelWidget> widgetType, IConfigType<?> type, int x, int y, int width, int height) {
        super(widgetType, type, x, y, width, height);

        content = type != null ? new TextComponent(type.getId()) : TextComponent.EMPTY;
        verticalAlignment = VerticalAlignment.CENTER;
        horizontalAlignment = HorizontalAlignment.LEFT;
        foreground = 0xFFFFFFFF;
        background = 0;
    }
}
