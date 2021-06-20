package dev.toma.configuration.api.client.screen;

import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.client.ScreenOpenContext;
import dev.toma.configuration.api.client.widget.ButtonWidget;
import dev.toma.configuration.api.client.widget.ConfigLayoutWidget;
import dev.toma.configuration.api.client.widget.IColumn;
import dev.toma.configuration.api.client.widget.WidgetType;
import dev.toma.configuration.api.type.CollectionType;
import dev.toma.configuration.client.WidgetList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

import java.util.Collection;

public class ConfigCollectionScreen extends WidgetScreen<CollectionType<?>> {

    public ConfigCollectionScreen(Screen parent, CollectionType<?> collection, ScreenOpenContext context) {
        super(parent, collection, context);
    }

    @Override
    public void layoutPost(ConfigLayoutWidget<?> layout) {
        layout.addColumn(IColumn.absolute(20, WidgetType.LABEL).setMargin(5));
    }

    @Override
    protected void initWidgets(WidgetList list) {
        int controlButtonCount = 2;
        int margin = 10;
        int totalEmptySpaceSize = margin * (controlButtonCount + 2); // 2 is added for empty spaces at left side and right side
        int widgetSize = (width - totalEmptySpaceSize) / controlButtonCount;

        ButtonWidget buttonBack = list.addControlWidget(WidgetType.BUTTON, margin, height - 25, widgetSize, 20);
        buttonBack.text = new StringTextComponent("Back");
        buttonBack.clicked = this::buttonBack_Clicked;
        ButtonWidget addElement = list.addControlWidget(WidgetType.BUTTON, 2 * margin + widgetSize, height - 25, widgetSize, 20);
        addElement.text = new StringTextComponent("Add element");
        addElement.clicked = this::buttonAddElement_Clicked;
    }

    @Override
    protected Collection<IConfigType<?>> getCollection(CollectionType<?> type) {
        return (Collection<IConfigType<?>>) type.get();
    }

    private void buttonBack_Clicked(double mouseX, double mouseY, int button) {

    }

    private void buttonAddElement_Clicked(double mouseX, double mouseY, int button) {

    }
}
