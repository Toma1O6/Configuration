package dev.toma.configuration.api.client.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.client.IClientSettings;
import dev.toma.configuration.api.client.screen.WidgetScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ConfigLayoutWidget<T extends IConfigType<?>> extends ConfigWidget<T> {

    final WidgetScreen<?> parent;
    final Layout layout;
    final List<ITextComponent> description;
    final IClientSettings settings;

    public ConfigLayoutWidget(T type, int x, int y, int width, int height, WidgetScreen<?> screen) {
        super(null, type, x, y, width, height);
        this.settings = screen.getOpeningContext().getModConfig().settings();
        this.parent = screen;
        this.layout = new Layout();
        this.description = Arrays.stream(type.getComments()).map(StringTextComponent::new).collect(Collectors.toList());
    }

    public void renderLayout(Consumer<Widget> render) {
        layout.drawElements(render);
    }

    public void columnInit() {
        layout.init();
    }

    public void addColumn(IColumn column) {
        layout.addColumn(column);
    }

    @Override
    public void resize(int x, int y, int width, int height) {
        super.resize(x, y, width, height);
        if (layout != null)
            layout.init();
    }

    @Override
    public void tick() {
        layout.forEach(ITickable::tick);
    }

    @Override
    public void save() {
        layout.forEach(Widget::save);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return invokeOnWidget(w -> w.mouseClicked(mouseX, mouseY, button));
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return invokeOnWidget(w -> w.mouseDragged(mouseX, mouseY, button, dragX, dragY));
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return invokeOnWidget(w -> w.mouseReleased(mouseX, mouseY, button));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return invokeOnWidget(w -> w.mouseScrolled(mouseX, mouseY, delta));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return invokeOnWidget(w -> w.keyPressed(keyCode, scanCode, modifiers));
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return invokeOnWidget(w -> w.charTyped(codePoint, modifiers));
    }

    private boolean invokeOnWidget(WidgetScreen.BooleanFunction<Widget> action) {
        boolean b = false;
        for (Widget widget : layout) {
            if (action.apply(widget))
                b = true;
        }
        return b;
    }

    private void showDescription(MatrixStack matrixStack, Minecraft mc, int mouseX, int mouseY) {
        parent.renderWrappedToolTip(matrixStack, description, mouseX, mouseY, mc.fontRenderer);
    }

    class Layout implements Iterable<Widget> {

        List<IColumn> columns;
        List<Widget> widgets;

        Layout() {
            this.columns = new ArrayList<>();
            this.widgets = new ArrayList<>();
        }

        @Override
        public Iterator<Widget> iterator() {
            return widgets.iterator();
        }

        void drawElements(Consumer<Widget> render) {
            widgets.forEach(render);
        }

        void addColumn(IColumn column) {
            columns.add(column);
        }

        void init() {
            widgets.clear();
            int layoutWidth = ConfigLayoutWidget.this.width;
            int relativeWidthPool = layoutWidth;
            // get relative width pool
            for (IColumn column : columns) {
                if (column.isAbsolute()) {
                    relativeWidthPool -= column.getColumnWidth(layoutWidth);
                }
            }
            int usedWidth = 0;
            int left = ConfigLayoutWidget.this.x;
            int top = ConfigLayoutWidget.this.y;
            int height = ConfigLayoutWidget.this.height;
            // place widgets
            Iterator<IColumn> it = columns.iterator();
            while (it.hasNext()) {
                IColumn column = it.next();
                boolean lastElement = !it.hasNext();
                int widgetWidth = column.getColumnWidth(relativeWidthPool);
                int colMargin = column.getMargin();
                int diff = 0;
                if (lastElement) {
                    diff = layoutWidth - (usedWidth + widgetWidth);
                }
                Widget widget = column.init(ConfigLayoutWidget.this.getConfigType(), ConfigLayoutWidget.this.settings, left + usedWidth + colMargin + diff, top, widgetWidth - colMargin, height);
                usedWidth += widgetWidth;
                widgets.add(widget);
            }
        }
    }
}
