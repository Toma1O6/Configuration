package dev.toma.configuration.api.client.screen;

import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.client.ScreenOpenContext;
import dev.toma.configuration.api.client.widget.*;
import dev.toma.configuration.api.type.CollectionType;
import dev.toma.configuration.client.WidgetList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

import java.util.Collection;
import java.util.Iterator;

@SuppressWarnings("unchecked")
public class ConfigCollectionScreen extends WidgetScreen<CollectionType<?>> {

    public ConfigCollectionScreen(Screen parent, CollectionType<?> collection, ScreenOpenContext context) {
        super(parent, collection, context);
    }

    @Override
    public void layoutPost(ConfigLayoutWidget<?> layout) {
        double value = 0;
        IColumn scaled = null;
        Iterator<IColumn> iterator = layout.getColumns().iterator();
        while (iterator.hasNext()) {
            IColumn column = iterator.next();
            if (column.getType() == WidgetType.LABEL) {
                if (!column.isAbsolute()) {
                    value += ((IColumn.Relative) column).getPart();
                }
                iterator.remove();
            } else if (!column.isAbsolute() && scaled == null) {
                scaled = column;
            }
        }
        if (scaled != null)
            ((IColumn.Relative) scaled).addPart(value);
        layout.addColumn(IColumn.absolute(20, WidgetType.LABEL).setMargin(5));
    }

    @Override
    protected void initWidgets(WidgetList list) {
        int controlButtonCount = 2;
        int margin = 10;
        int totalEmptySpaceSize = margin * (controlButtonCount + 2); // 2 is added for empty spaces at left side and right side
        int widgetSize = (width - totalEmptySpaceSize) / controlButtonCount;

        ButtonWidget buttonBack = list.addControlWidget(WidgetType.BUTTON, margin, height - 30, widgetSize, 20);
        buttonBack.text = new StringTextComponent("Back");
        buttonBack.clicked = this::buttonBack_Clicked;
        ButtonWidget addElement = list.addControlWidget(WidgetType.BUTTON, 2 * margin + widgetSize, height - 30, widgetSize, 20);
        addElement.text = new StringTextComponent("Add element");
        addElement.clicked = this::buttonAddElement_Clicked;
        if (type.hasLockedSize()) {
            addElement.visibilityState = WidgetState.DISABLED;
            addElement.borderColor = 0xFF444444;
            addElement.foreground = 0xFF444444;
        }
    }

    @Override
    protected Collection<IConfigType<?>> getCollection(CollectionType<?> type) {
        return (Collection<IConfigType<?>>) type.get();
    }

    private void buttonBack_Clicked(double mouseX, double mouseY, int button) {
        minecraft.setScreen(parentScreen);
    }

    private <A extends IConfigType<?>> void buttonAddElement_Clicked(double mouseX, double mouseY, int button) {
        CollectionType<A> collectionType = (CollectionType<A>) type;
        A a = collectionType.createElement();
        collectionType.add(a);
        WidgetList widgets = getWidgets();
        widgets.markForUpdate();
        widgets.init(this::initWidgets, () -> getCollection(type));
    }
}
