package dev.toma.configuration.api.client.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.client.IClientSettings;
import dev.toma.configuration.api.client.screen.WidgetScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Deprecated
public class ConfigLayoutWidget<T extends IConfigType<?>> extends ConfigWidget<T> {

    final WidgetScreen<?> parent;
    final Layout layout;
    final List<Component> description;
    final IClientSettings settings;
    int mouseOverTime;
    boolean hovered;

    public ConfigLayoutWidget(T type, int x, int y, int width, int height, WidgetScreen<?> screen) {
        super(null, type, x, y, width, height);
        this.settings = screen.getOpeningContext().getModConfig().settings();
        this.parent = screen;
        this.layout = new Layout();
        this.description = Arrays.stream(type.getComments()).map(TextComponent::new).collect(Collectors.toList());
    }

    public void renderWidget(Consumer<Widget> render, PoseStack stack, Minecraft mc, int mouseX, int mouseY) {
        layout.drawElements(render);
        hovered = isMouseOver(mouseX, mouseY);
        if (mouseOverTime >= 20) {
            showDescription(stack, mc, mouseX, mouseY);
        }
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
        mouseOverTime = hovered ? mouseOverTime + 1 : 0;
    }

    @Override
    public void save() {
        layout.forEach(Widget::save);
    }

    public List<IColumn> getColumns() {
        return layout.getColumns();
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
            if (widget.visibilityState.isEnabled() && action.apply(widget)) {
                b = true;
                mouseOverTime = 0;
            }
        }
        return b;
    }

    private void showDescription(PoseStack matrixStack, Minecraft mc, int mouseX, int mouseY) {
        parent.renderTooltip(matrixStack, description, Optional.empty(), mouseX, mouseY, mc.font);
    }

    class Layout implements Iterable<Widget> {

        List<IColumn> columns;
        List<Widget> widgets;

        Layout() {
            this.columns = new ArrayList<>();
            this.widgets = new ArrayList<>();
        }

        public List<IColumn> getColumns() {
            return columns;
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
            widgets.forEach(Widget::save);
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
                widget.assignParent(parent);
                usedWidth += widgetWidth;
                widgets.add(widget);
            }
        }
    }
}
