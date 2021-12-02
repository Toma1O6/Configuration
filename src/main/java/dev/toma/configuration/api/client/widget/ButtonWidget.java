package dev.toma.configuration.api.client.widget;

import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.client.HorizontalAlignment;
import dev.toma.configuration.api.client.VerticalAlignment;
import net.minecraft.network.chat.Component;

@Deprecated
public class ButtonWidget extends Widget {

    public int borderColor;
    public ClickCallback clicked;
    public Component text;
    public HorizontalAlignment horizontalAlignment;
    public VerticalAlignment verticalAlignment;

    public ButtonWidget(WidgetType<? extends ButtonWidget> widgetType, IConfigType<?> type, int x, int y, int width, int height) {
        super(widgetType, type, x, y, width, height);
        background = 0xFF << 24;
        foreground = 0xFFFFFFFF;
        borderColor = 0xFFFFFFFF;
        horizontalAlignment = HorizontalAlignment.CENTER;
        verticalAlignment = VerticalAlignment.CENTER;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (!visibilityState.isDisabled() && isMouseOver(mouseX, mouseY) && mouseButton == 0) {
            if (clicked != null)
                clicked.onClick(mouseX, mouseY, mouseButton);
            playPressSound();
            return true;
        }
        return false;
    }

    public interface ClickCallback {
        void onClick(double mouseX, double mouseY, int button);
    }
}
