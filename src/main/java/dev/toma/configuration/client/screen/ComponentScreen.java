package dev.toma.configuration.client.screen;

import com.google.common.collect.Queues;
import dev.toma.configuration.api.type.AbstractConfigType;
import dev.toma.configuration.client.IModID;
import dev.toma.configuration.client.screen.component.Component;
import dev.toma.configuration.client.screen.component.ConfigComponent;
import dev.toma.configuration.client.screen.component.TextFieldComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ComponentScreen extends Screen implements IModID {

    final String modID;
    protected List<Component> components = new ArrayList<>();
    public TextFieldComponent<?> selectedTextField;
    Queue<Consumer<ComponentScreen>> queue = Queues.newArrayDeque();

    public ComponentScreen(ITextComponent title, String modID) {
        super(title);
        this.modID = modID;
    }

    @Override
    public String getModID() {
        return modID;
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        components.clear();
        super.init(minecraft, width, height);
    }

    public void addComponent(Component component) {
        components.add(component);
    }

    public int getTextColor() {
        return 0x999999;
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
        renderTooltip(Arrays.stream(desc).collect(Collectors.toList()), mouseX, mouseY, font);
    }

    @Override
    public void onClose() {
        if(selectedTextField != null) {
            selectedTextField.onUnselect();
            selectedTextField = null;
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        boolean hoveredOnce = false;
        for (Component component : components) {
            boolean mouseOver = component.isMouseOver(mouseX, mouseY);
            boolean componentHovered = !hoveredOnce && mouseOver;
            component.drawComponent(font, mouseX, mouseY, partialTicks, componentHovered);
            if(componentHovered) {
                hoveredOnce = true;
            }
        }
    }

    @Override
    public void tick() {
        Consumer<ComponentScreen> event;
        while ((event = queue.poll()) != null) {
            event.accept(this);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(selectedTextField != null && !selectedTextField.isMouseOver(mouseX, mouseY)) {
            selectedTextField.onUnselect();
            selectedTextField = null;
        }
        for (Component component : components) {
            if(component.isMouseOver(mouseX, mouseY) && component.hasClicked(mouseX, mouseY, button)) {
                minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                component.processClicked(mouseX, mouseY);
                if(component instanceof TextFieldComponent) {
                    if(selectedTextField == component) {
                        selectedTextField.onUnselect();
                        selectedTextField = null;
                    } else selectedTextField = (TextFieldComponent<?>) component;
                }
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(selectedTextField != null) {
            selectedTextField.keyPressed(keyCode, scanCode, modifiers);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if(selectedTextField != null) {
            selectedTextField.charTyped(codePoint, modifiers);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        for (Component component : components) {
            if(component.isMouseOver(mouseX, mouseY)) {
                component.mouseDragged(mouseX, mouseY, button, dragX, dragY);
                return true;
            }
        }
        return false;
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
