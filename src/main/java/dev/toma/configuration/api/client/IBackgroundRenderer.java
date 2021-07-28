package dev.toma.configuration.api.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.toma.configuration.api.client.screen.WidgetScreen;
import dev.toma.configuration.api.client.widget.ITickable;
import net.minecraft.client.Minecraft;

/**
 * Interface handling render of config UI.
 * Allows simple modifications to config UI without the need to completely
 * reimplement it.
 */
public interface IBackgroundRenderer extends ITickable {

    /**
     * Draws screen background
     * @param mc Running mc instance
     * @param stack Current pose stack
     * @param mouseX X mouse position
     * @param mouseY Y mouse position
     * @param partialTicks partial render ticks, used for interpolation
     * @param screen Screen being rendered
     */
    void drawBackground(Minecraft mc, PoseStack stack, int mouseX, int mouseY, float partialTicks, WidgetScreen<?> screen);

    /**
     * Draws extra stuff into screen header
     * @param mc Running mc instance
     * @param stack Current pose stack
     * @param x Header x coordinate
     * @param y Header y coordinate
     * @param headerWidth Header width
     * @param headerHeight Header height
     * @param mouseX X mouse position
     * @param mouseY Y mouse position
     * @param partialTicks partial render ticks, used for interpolation
     * @param screen Screen being rendered
     */
    void drawHeaderBackground(Minecraft mc, PoseStack stack, int x, int y, int headerWidth, int headerHeight, int mouseX, int mouseY, float partialTicks, WidgetScreen<?> screen);

    /**
     * Draws extra stuff into screen footer
     * @param mc Running mc instance
     * @param stack Current pose stack
     * @param x Footer x coordinate
     * @param y Footer y coordinate
     * @param footerWidth Footer width
     * @param footerHeight Footer height
     * @param mouseX X mouse position
     * @param mouseY Y mouse position
     * @param partialTicks partial render ticks, used for interpolation
     * @param screen Screen being rendered
     */
    void drawFooterBackground(Minecraft mc, PoseStack stack, int x, int y, int footerWidth, int footerHeight, int mouseX, int mouseY, float partialTicks, WidgetScreen<?> screen);

    /**
     * Draws scrollbar
     * @param mc Running mc instance
     * @param stack Current pose stack
     * @param index Scroll index
     * @param displayAmount Amount of widgets displayed on page
     * @param totalCount Total amount of widgets
     * @param width Screen width
     * @param y Scrollbar Y position
     * @param height Screen height excluding header and footer
     */
    void drawScrollbar(Minecraft mc, PoseStack stack, int index, int displayAmount, int totalCount, int width, int y, int height);

    /**
     * @return Default label color. Applied via default label style
     */
    int getDefaultLabelForegroundColor();

    /**
     * @return Default title color, color is same as {@link IBackgroundRenderer#getDefaultLabelForegroundColor()} in default
     * implementation
     */
    int getTitleColor();
}
