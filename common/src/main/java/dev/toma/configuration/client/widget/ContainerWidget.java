package dev.toma.configuration.client.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public abstract class ContainerWidget extends AbstractWidget implements ContainerEventHandler {

    private final List<GuiEventListener> listeners = new ArrayList<>();
    private final List<AbstractWidget> widgets = new ArrayList<>();
    private GuiEventListener focused;
    private boolean dragging;

    public ContainerWidget(int x, int y, int w, int h, Component component) {
        super(x, y, w, h, component);
    }

    public <L extends GuiEventListener> L addGuiEventListener(L listener) {
        this.listeners.add(listener);
        return listener;
    }

    public void removeGuiEventListener(GuiEventListener listener) {
        listeners.remove(listener);
    }

    public <W extends AbstractWidget> W addRenderableWidget(W widget) {
        widgets.add(widget);
        return addGuiEventListener(widget);
    }

    public void removeWidget(AbstractWidget widget) {
        widgets.remove(widget);
        removeGuiEventListener(widget);
    }

    public void clear() {
        listeners.clear();
        widgets.clear();
        focused = null;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        widgets.forEach(widget -> widget.render(graphics, mouseX, mouseY, partialTicks));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        boolean result = ContainerEventHandler.super.mouseClicked(event, doubleClick);
        if (!result && this.focused != null) {
            this.setFocused(null);
        }
        return result;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        return ContainerEventHandler.super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double p_231045_6_, double p_231045_8_) {
        return ContainerEventHandler.super.mouseDragged(event, p_231045_6_, p_231045_8_);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amountX, double amountY) {
        return ContainerEventHandler.super.mouseScrolled(mouseX, mouseY, amountX, amountY);
    }

    @Override
    public void mouseMoved(double p_212927_1_, double p_212927_3_) {
        ContainerEventHandler.super.mouseMoved(p_212927_1_, p_212927_3_);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        return ContainerEventHandler.super.keyPressed(event);
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        return ContainerEventHandler.super.keyReleased(event);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return listeners;
    }

    @Override
    public boolean isDragging() {
        return dragging;
    }

    @Override
    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    @Override
    public GuiEventListener getFocused() {
        return focused;
    }

    @Override
    public void setFocused(GuiEventListener focused) {
        if (this.focused != null) {
            this.focused.setFocused(false);
        }
        if (focused != null) {
            focused.setFocused(true);
        }
        this.focused = focused;
    }
}
