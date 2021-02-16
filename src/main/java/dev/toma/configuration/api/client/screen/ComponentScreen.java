package dev.toma.configuration.api.client.screen;

import com.google.common.collect.Queues;
import dev.toma.configuration.api.client.IModID;
import dev.toma.configuration.api.client.component.Component;
import dev.toma.configuration.api.client.component.ConfigComponent;
import dev.toma.configuration.api.client.component.TextFieldComponent;
import dev.toma.configuration.api.type.AbstractConfigType;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.SoundEvents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ComponentScreen extends GuiScreen implements IModID {

    final GuiScreen parentScreen;
    final String modID;
    protected List<Component> components = new ArrayList<>();
    public TextFieldComponent<?> selectedTextField;
    Queue<Consumer<ComponentScreen>> queue = Queues.newArrayDeque();
    protected int textColor;

    public ComponentScreen(GuiScreen parentScreen, String modID, int textColor) {
        this.parentScreen = parentScreen;
        this.modID = modID;
        this.textColor = textColor;
    }

    public void renderBackground() {
        this.drawDefaultBackground();
    }

    @Override
    public String getModID() {
        return modID;
    }

    @Override
    public void initGui() {
        components.clear();
    }

    public void addComponent(Component component) {
        components.add(component);
    }

    public int getTextColor() {
        return textColor;
    }

    public void renderHoveredInfo(int mouseX, int mouseY) {
        for (Component component : components) {
            if(component.isMouseOver(mouseX, mouseY) && component instanceof ConfigComponent<?>) {
                this.drawComments((ConfigComponent<?>) component, mouseX, mouseY);
            }
        }
    }

    protected void drawComments(ConfigComponent<?> configComponent, int mouseX, int mouseY) {
        AbstractConfigType<?> type = configComponent.getConfigElement();
        String[] desc = type.getComments();
        drawHoveringText(Arrays.stream(desc).collect(Collectors.toList()), mouseX, mouseY, fontRenderer);
    }

    @Override
    public void onGuiClosed() {
        if(selectedTextField != null) {
            selectedTextField.onUnselect();
            selectedTextField = null;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        boolean hoveredOnce = false;
        for (Component component : components) {
            boolean mouseOver = component.isMouseOver(mouseX, mouseY);
            boolean componentHovered = !hoveredOnce && mouseOver;
            component.drawComponent(fontRenderer, mouseX, mouseY, partialTicks, componentHovered);
            if(componentHovered) {
                hoveredOnce = true;
            }
        }
    }

    public void tick() {
        Consumer<ComponentScreen> event;
        while ((event = queue.poll()) != null) {
            event.accept(this);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(selectedTextField != null && !selectedTextField.isMouseOver(mouseX, mouseY)) {
            selectedTextField.onUnselect();
            selectedTextField = null;
        }
        for (Component component : components) {
            if(component.isMouseOver(mouseX, mouseY) && component.hasClicked(mouseX, mouseY, button)) {
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                component.processClicked(mouseX, mouseY);
                if(component instanceof TextFieldComponent) {
                    if(selectedTextField == component) {
                        selectedTextField.onUnselect();
                        selectedTextField = null;
                    } else selectedTextField = (TextFieldComponent<?>) component;
                }
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(selectedTextField != null) {
            selectedTextField.keyPressed(keyCode);
            selectedTextField.charTyped(typedChar);
        }
        if(keyCode == 1) {
            mc.displayGuiScreen(parentScreen);
        }
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int button, long time) {
        for (Component component : components) {
            if(component.isMouseOver(mouseX, mouseY)) {
                component.mouseDragged(mouseX, mouseY, button, time);
            }
        }
    }

    public boolean isSelected(TextFieldComponent<?> component) {
        return component == selectedTextField;
    }

    public void sendUpdate() {
        for (Component component : components) {
            if(component instanceof ConfigComponent<?>) {
                ((ConfigComponent<?>) component).onUpdate();
            }
        }
    }

    public void scheduleUpdate(Consumer<ComponentScreen> componentScreenConsumer) {
        queue.add(componentScreenConsumer);
    }
}
