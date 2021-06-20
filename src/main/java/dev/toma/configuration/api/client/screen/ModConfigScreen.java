package dev.toma.configuration.api.client.screen;

import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.client.ScreenOpenContext;
import dev.toma.configuration.api.client.widget.ButtonWidget;
import dev.toma.configuration.api.client.widget.WidgetType;
import dev.toma.configuration.api.type.ObjectType;
import dev.toma.configuration.client.WidgetList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

public class ModConfigScreen extends WidgetScreen<ObjectType> {

    public ModConfigScreen(Screen parent, ObjectType object, ScreenOpenContext context) {
        super(parent, object, context);
    }

    @Override
    protected void initWidgets(WidgetList list) {
        ButtonWidget backToMenu = list.addControlWidget(WidgetType.BUTTON, 10, height - 25, width - 20, 20);
        backToMenu.clicked = this::backToMenu_Clicked;
        backToMenu.text = new StringTextComponent("Back");
    }

    @Override
    protected Collection<IConfigType<?>> getCollection(ObjectType type) {
        return type.get().values().stream().sorted(Comparator.comparingInt(IConfigType::getSortIndex)).collect(Collectors.toList());
    }

    private void backToMenu_Clicked(double mouseX, double mouseY, int button) {
        minecraft.displayGuiScreen(parentScreen);
    }
}
