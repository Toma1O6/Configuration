package dev.toma.configuration.api.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.ModConfig;
import dev.toma.configuration.api.client.IBackgroundRenderer;
import dev.toma.configuration.api.client.ScreenOpenContext;
import dev.toma.configuration.api.client.widget.ConfigLayoutWidget;
import dev.toma.configuration.api.client.widget.ITickable;
import dev.toma.configuration.api.client.widget.Widget;
import dev.toma.configuration.client.WidgetList;
import dev.toma.configuration.internal.FileTracker;
import dev.toma.configuration.util.Callback;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

import java.util.Collection;

public abstract class WidgetScreen<T extends IConfigType<?>> extends Screen implements IConfigurationScreen {

    protected final Screen parentScreen;
    protected final T type;
    protected int headerHeight = 30;
    protected int footerHeight = 40;
    private final WidgetList widgets;
    private final ScreenOpenContext context;
    protected final Callback<Integer> scrollIndexCallback;
    protected final IBackgroundRenderer backgroundRenderer;
    protected int scrollIndex;
    protected int displayCount;

    public WidgetScreen(Screen screen, T type, ScreenOpenContext context) {
        super(new TextComponent(type.getId()));
        this.parentScreen = screen;
        this.type = type;
        this.context = context;
        this.widgets = new WidgetList(this);
        this.backgroundRenderer = context.getModConfig().settings().getBackgroundRenderer();

        scrollIndexCallback = widgets::scrollIndexChanged;
    }

    /**
     * Use this method to create "static" widgets, i.e. back to menu buttons etc.
     * Basically all buttons, which will always be shown on the screen regardless of
     * current scroll offset.
     * @param list Widget container containing all widgets for this screen
     */
    protected abstract void initWidgets(WidgetList list);

    /**
     * Used to extract all config types from specified config type container
     * @param t Type container
     * @return Collection of all config types which are contained in this config container
     */
    protected abstract Collection<IConfigType<?>> getCollection(T t);

    @Override
    public void onClose() {
        minecraft.setScreen(parentScreen);
    }

    @Override
    public void removed() {
        widgets.forEach(Widget::save);
        if (!(parentScreen instanceof IConfigurationScreen)) {
            FileTracker.INSTANCE.runConfigUpdateAsync(context, FileTracker.UpdateAction.WRITE);
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        backgroundRenderer.drawBackground(minecraft, poseStack, mouseX, mouseY, partialTicks, this);
        renderHeaderAndFooter(poseStack, mouseX, mouseY, partialTicks, backgroundRenderer);
        backgroundRenderer.drawScrollbar(minecraft, poseStack, scrollIndex, displayCount, widgets.configElementCount(), width, headerHeight, height - footerHeight - headerHeight);
        widgets.render(poseStack, minecraft, mouseX, mouseY, partialTicks, scrollIndex);
    }

    @Override
    public void tick() {
        widgets.forEach(ITickable::tick);
        backgroundRenderer.tick();
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
        boolean widgetScrolled = invokeOnWidget(w -> w.mouseScrolled(mouseX, mouseY, delta));
        boolean screenScrolled = false;
        if (!widgetScrolled) {
            int target = (int) (scrollIndex - delta);
            if (target != scrollIndex) {
                if (target >= 0 && target <= widgets.configElementCount() - displayCount) {
                    setScrollIndex(target);
                    screenScrolled = true;
                }
            }
        }
        return widgetScrolled || screenScrolled;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return invokeOnWidget(w -> w.keyPressed(keyCode, scanCode, modifiers));
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return invokeOnWidget(w -> w.charTyped(codePoint, modifiers));
    }

    private boolean invokeOnWidget(BooleanFunction<Widget> action) {
        boolean b = false;
        for (Widget widget : widgets) {
            if (widget.visibilityState.isEnabled() && action.apply(widget))
                b = true;
        }
        return b;
    }

    @Override
    public ModConfig getConfiguration() {
        return context.getModConfig();
    }

    /**
     * @return Opening context containing ModConfig
     */
    public ScreenOpenContext getOpeningContext() {
        return context;
    }

    @Override
    protected final void init() {
        this.displayCount = editDisplayAmount(height - headerHeight - footerHeight - 10) / 25;
        widgets.setDisplayAmount(displayCount);
        setScrollIndex(0);
        setWidgetPanelSize(widgets);
        widgets.init(this::initWidgets, () -> getCollection(type));
    }

    protected void setWidgetPanelSize(WidgetList list) {
        list.resize(40, headerHeight + 5, width - 80, height - headerHeight - footerHeight - 5);
    }

    /**
     * Called when layout is initialized and all columns are added.
     * This method can be used to insert additional columns, like collection element controls
     * @param layout Layout widget
     */
    public void layoutPost(ConfigLayoutWidget<?> layout) {
    }

    public WidgetList getWidgets() {
        return widgets;
    }

    protected int editDisplayAmount(int displayCount) {
        return displayCount;
    }

    protected void setScrollIndex(int scrollIndex) {
        this.scrollIndex = scrollIndex;
        this.scrollIndexCallback.call(scrollIndex);
    }

    protected void renderHeaderAndFooter(PoseStack poses, int mouseX, int mouseY, float partialTicks, IBackgroundRenderer renderer) {
        poses.pushPose(); // render header and footer above background
        poses.translate(0, 0, 1);
        renderer.drawHeaderBackground(minecraft, poses, 0, 0, width, headerHeight, mouseX, mouseY, partialTicks, this);
        renderer.drawFooterBackground(minecraft, poses, 0, height - footerHeight, width, footerHeight, mouseX, mouseY, partialTicks, this);
        Widget.drawCenteredString(type.getId().toUpperCase(), poses, font, 0, 0, width, headerHeight, renderer.getTitleColor());
        poses.popPose();
    }

    public interface BooleanFunction<T> {

        boolean apply(T t);
    }
}
