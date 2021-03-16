package dev.toma.configuration.internal.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.client.GuiScrollingList;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.ModContainer;

import java.util.ArrayList;

public class ConfigGuiModList extends GuiScrollingList {

    private static final ResourceLocation VERSION_CHECK_ICONS = new ResourceLocation(ForgeVersion.MOD_ID, "textures/gui/version_check_icons.png");

    private ConfigGui parent;
    private ArrayList<ModContainer> mods;

    public ConfigGuiModList(ConfigGui parent, ArrayList<ModContainer> mods, int listWidth, int slotHeight) {
        super(parent.getMinecraftInstance(), listWidth, parent.height, 32, parent.height - 88 + 4, 10, slotHeight, parent.width, parent.height);
        this.parent = parent;
        this.mods = mods;
    }

    @Override
    protected int getSize() {
        return mods.size();
    }

    @Override
    protected void elementClicked(int index, boolean doubleClick) {
        this.parent.selectModIndex(index);
    }

    @Override
    protected boolean isSelected(int index) {
        return this.parent.modIndexSelected(index);
    }

    @Override
    protected void drawBackground() {
        this.parent.drawDefaultBackground();
    }

    @Override
    protected int getContentHeight() {
        return (this.getSize()) * 35 + 1;
    }

    ArrayList<ModContainer> getMods() {
        return mods;
    }

    public int getRight() {
        return right;
    }

    public int getBottom() {
        return bottom;
    }

    public int getListWidth() {
        return listWidth;
    }

    public int setAndGetSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        return selectedIndex;
    }

    @Override
    protected void drawSlot(int idx, int right, int top, int height, Tessellator tess) {
        ModContainer mc = mods.get(idx);
        String name = StringUtils.stripControlCodes(mc.getName());
        String version = StringUtils.stripControlCodes(mc.getDisplayVersion());
        FontRenderer font = this.parent.getFontRenderer();
        ForgeVersion.CheckResult vercheck = ForgeVersion.getResult(mc);

        if (Loader.instance().getModState(mc) == LoaderState.ModState.DISABLED) {
            font.drawString(font.trimStringToWidth(name, listWidth - 10), this.left + 3, top + 2, 0xFF2222);
            font.drawString(font.trimStringToWidth(version, listWidth - (5 + height)), this.left + 3, top + 12, 0xFF2222);
            font.drawString(font.trimStringToWidth("DISABLED", listWidth - 10), this.left + 3, top + 22, 0xFF2222);
        } else {
            font.drawString(font.trimStringToWidth(name, listWidth - 10), this.left + 3, top + 2, 0xFFFFFF);
            font.drawString(font.trimStringToWidth(version, listWidth - (5 + height)), this.left + 3, top + 12, 0xCCCCCC);
            font.drawString(font.trimStringToWidth(mc.getMetadata() != null ? mc.getMetadata().getChildModCountString() : "Metadata not found", listWidth - 10), this.left + 3, top + 22, 0xCCCCCC);

            if (vercheck.status.shouldDraw()) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(VERSION_CHECK_ICONS);
                GlStateManager.color(1, 1, 1, 1);
                GlStateManager.pushMatrix();
                Gui.drawModalRectWithCustomSizedTexture(right - (height / 2 + 4), top + (height / 2 - 4), vercheck.status.getSheetOffset() * 8, (vercheck.status.isAnimated() && ((System.currentTimeMillis() / 800 & 1)) == 1) ? 8 : 0, 8, 8, 64, 16);
                GlStateManager.popMatrix();
            }
        }
    }


}