package dev.toma.configuration.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.ModConfig;
import dev.toma.configuration.api.TypeKey;
import dev.toma.configuration.api.client.*;
import dev.toma.configuration.api.client.screen.WidgetScreen;
import dev.toma.configuration.api.client.widget.ConfigLayoutWidget;
import dev.toma.configuration.api.client.widget.Widget;
import dev.toma.configuration.api.client.widget.WidgetState;
import dev.toma.configuration.api.client.widget.WidgetType;
import net.minecraft.client.Minecraft;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Deprecated
public class WidgetList implements Iterable<Widget> {

    int x;
    int y;
    int width;
    int height;
    int scroll;
    int displayAmount;
    boolean loaded;
    WidgetScreen<?> parent;
    List<Widget> controls = new ArrayList<>();
    List<ConfigLayoutWidget<?>> configElements = new ArrayList<>();

    public WidgetList(WidgetScreen<?> parent) {
        this.parent = parent;
    }

    public void resize(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        reorderElements();
    }

    public void setDisplayAmount(int displayAmount) {
        this.displayAmount = displayAmount;
    }

    public void scrollIndexChanged(int index) {
        scroll = index;
        reorderElements();
    }

    public void render(PoseStack stack, Minecraft mc, int mouseX, int mouseY, float partialTicks, int index) {
        IWidgetManager manager = parent.getConfiguration().settings().getWidgetManager();
        stack.pushPose();
        stack.translate(0, 0, 1);
        for (Widget control : controls) {
            renderWidget(manager, control, stack, mc, mouseX, mouseY, partialTicks);
        }
        stack.popPose();
        for (int i = index; i < Math.min(configElements.size(), index + displayAmount); i++) {
            configElements.get(i).renderWidget(widget -> renderWidget(manager, widget, stack, mc, mouseX, mouseY, partialTicks), stack, mc, mouseX, mouseY);
        }
    }

    public void markForUpdate() {
        this.loaded = false;
        configElements.clear();
    }

    public void init(Consumer<WidgetList> action, Supplier<Collection<IConfigType<?>>> supplier) {
        controls.clear();
        action.accept(this);
        if (!loaded) {
            addConfigTypes(supplier.get());
            loaded = true;
        }
        scrollIndexChanged(scroll);
    }

    public void addConfigTypes(Collection<IConfigType<?>> unsortedCollection) {
        unsortedCollection.stream().sorted(Comparator.comparingInt(t -> t.getType().getSortIndex())).forEach(this::addConfigWidget);
    }

    @Override
    public Iterator<Widget> iterator() {
        return new Itr();
    }

    public <W extends Widget> W addControlWidget(WidgetType<W> type, int x, int y, int width, int height) {
        return addControlWidget(type, x, y, width, height, null);
    }

    public <W extends Widget> W addControlWidget(WidgetType<W> type, int x, int y, int width, int height, String style) {
        W w = type.instantiateWidget(null, parent.getConfiguration().settings(), x, y, width, height, style);
        w.assignParent(parent);
        controls.add(w);
        return w;
    }

    public int configElementCount() {
        return configElements.size();
    }

    private <T> void addConfigWidget(IConfigType<T> type) {
        ConfigLayoutWidget<? extends IConfigType<T>> layout = new ConfigLayoutWidget<>(type, 0, 0, 100, 20, parent);
        ScreenOpenContext ctx = parent.getOpeningContext();
        ModConfig config = ctx.getModConfig();
        IClientSettings settings = config.settings();
        IWidgetManager manager = settings.getWidgetManager();
        TypeKey typeKey = type.getType();
        IWidgetPlacer placer = manager.getPlacement(typeKey);
        if (placer == null) {
            Configuration.LOGGER.error("Found null widget placer for type {} while trying to create config screen for {} mod. Ignoring element.", typeKey, ctx.getModID());
            return;
        }
        placer.place(type, layout);
        parent.layoutPost(layout);
        layout.columnInit();
        configElements.add(layout);
    }

    private void reorderElements() {
        int spaceTakenByWidgets = displayAmount * 20;
        int emptySpace = height - spaceTakenByWidgets;
        int margin = 20 + emptySpace / displayAmount;
        for (int i = 0; i < configElements.size(); i++) {
            int j = i - scroll;
            int yOffset = j * margin;
            Widget widget = configElements.get(i);
            widget.visibilityState = j >= 0 && j < displayAmount ? WidgetState.VISIBLE : WidgetState.HIDDEN;
            widget.resize(x, y + yOffset, width, 20);
        }
    }

    private <W extends Widget> void renderWidget(IWidgetManager manager, W widget, PoseStack stack, Minecraft mc, int mouseX, int mouseY, float delta) {
        if (widget.visibilityState.isHidden())
            return;
        WidgetType<W> type = (WidgetType<W>) widget.getWidgetType();
        IWidgetRenderer<W> renderer = manager.getRenderer(type);
        if (renderer == null) {
            throw new NullPointerException("Missing renderer for " + type);
        }
        renderer.renderWidget(widget, stack, mc, mouseX, mouseY, delta);
    }

    private class Itr implements Iterator<Widget> {
        private int index;
        private final int list1Size = controls.size();
        private boolean secondList;

        @Override
        public boolean hasNext() {
            if (secondList) {
                return index - list1Size < configElements.size();
            } else {
                if (index == list1Size) {
                    return (index - list1Size) < configElements.size();
                } else {
                    return index < list1Size;
                }
            }
        }

        @Override
        public Widget next() {
            if (secondList)
                return configElements.get(index++ - list1Size);
            if (!secondList) {
                if (index >= list1Size) {
                    secondList = true;
                    ++index;
                    return configElements.get(0);
                }
            }
            return controls.get(index++);
        }
    }
}
