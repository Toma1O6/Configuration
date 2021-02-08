package dev.toma.configuration.client.screen;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.configuration.api.type.AbstractConfigType;
import dev.toma.configuration.client.IModID;
import dev.toma.configuration.client.screen.component.Component;
import dev.toma.configuration.client.screen.component.ConfigComponent;
import dev.toma.configuration.client.screen.component.TextFieldComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

    public <C extends Component> C addComponent(C component) {
        components.add(component);
        return component;
    }

    public void renderHoveredInfo(MatrixStack stack, int mouseX, int mouseY) {
        for (Component component : components) {
            if(component.isMouseOver(mouseX, mouseY) && component instanceof ConfigComponent<?>) {
                this.drawComments(stack, (ConfigComponent<?>) component, mouseX, mouseY);
            }
        }
    }

    protected void drawComments(MatrixStack stack, ConfigComponent<?> configComponent, int mouseX, int mouseY) {
        AbstractConfigType<?> type = configComponent.getConfigElement();
        String[] desc = type.getComments();
        renderWrappedToolTip(stack, Arrays.stream(desc).map(StringTextComponent::new).collect(Collectors.toList()), mouseX, mouseY, font);
    }

    @Override
    public void closeScreen() {
        if(selectedTextField != null) {
            selectedTextField.onUnselect();
            selectedTextField = null;
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        boolean hoveredOnce = false;
        for (Component component : components) {
            boolean mouseOver = component.isMouseOver(mouseX, mouseY);
            boolean componentHovered = !hoveredOnce && mouseOver;
            component.drawComponent(matrixStack, font, mouseX, mouseY, partialTicks, componentHovered);
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
        for (Component component : components) {
            if(component.isMouseOver(mouseX, mouseY) && component.hasClicked(mouseX, mouseY, button)) {
                component.processClicked(mouseX, mouseY);
                if(component instanceof TextFieldComponent) {
                    if(selectedTextField == component) {
                        selectedTextField.onUnselect();
                        selectedTextField = null;
                    } else selectedTextField = (TextFieldComponent<?>) component;
                }
            }
        }
        if(selectedTextField != null && !selectedTextField.isMouseOver(mouseX, mouseY)) {
            selectedTextField.onUnselect();
            selectedTextField = null;
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
