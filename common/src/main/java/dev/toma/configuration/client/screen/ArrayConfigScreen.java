package dev.toma.configuration.client.screen;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.client.theme.ConfigTheme;
import dev.toma.configuration.client.theme.adapter.DisplayAdapter;
import dev.toma.configuration.client.widget.ConfigEntryWidget;
import dev.toma.configuration.client.widget.ThemedButtonWidget;
import dev.toma.configuration.client.widget.render.TextureRenderer;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.adapter.TypeAdapterManager;
import dev.toma.configuration.config.value.AbstractArrayValue;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.ValueData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.message.FormattedMessage;

import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ArrayConfigScreen<V, C extends AbstractArrayValue<V>> extends AbstractConfigScreen {

    public static final Component ADD_ELEMENT = Component.translatable("text.configuration.value.add_element");
    public static final ResourceLocation REMOVE_ICON = ResourceLocation.fromNamespaceAndPath(Configuration.MODID, "textures/icons/remove.png");

    public final C array;
    private final boolean fixedSize;

    private Supplier<Integer> sizeSupplier = () -> 0;
    private DummyConfigValueFactory valueFactory;
    private ElementAddHandler addHandler;
    private ElementRemoveHandler<V[]> removeHandler;

    public ArrayConfigScreen(ConfigHolder<?> holder, C array, Screen previous) {
        super(array.getValueData().getTitle(), previous, holder);
        this.array = array;
        this.fixedSize = array.isFixedSize();
    }

    public void fetchSize(Supplier<Integer> integerSupplier) {
        this.sizeSupplier = integerSupplier;
    }

    public void valueFactory(DummyConfigValueFactory factory) {
        this.valueFactory = factory;
    }

    public void addElement(ElementAddHandler handler) {
        this.addHandler = handler;
    }

    public void removeElement(ElementRemoveHandler<V[]> handler) {
        this.removeHandler = handler;
    }

    @Override
    protected void init() {
        final int viewportMin = HEADER_HEIGHT;
        final int viewportHeight = this.height - viewportMin - FOOTER_HEIGHT;
        this.pageSize = (viewportHeight - 20) / 25;
        this.correctScrollingIndex(this.getTotalSize());
        int errorOffset = (viewportHeight - 20) - (this.pageSize * 25 - 5);
        int offset = 0;

        Class<?> compType = array.get().getClass().getComponentType();
        DisplayAdapter adapter = this.theme.getAdapter(compType);
        TypeAdapter.AdapterContext context = array.getSerializationContext();
        Field owner = context.getOwner();
        for (int i = this.index; i < this.index + this.pageSize; i++) {
            int j = i - this.index;
            if (i >= this.sizeSupplier.get()) {
                ThemedButtonWidget addElement = addRenderableWidget(new ThemedButtonWidget(30, viewportMin + 10 + j * 25 + offset, this.width - 60, 20, ADD_ELEMENT, theme));
                addElement.setBackgroundRenderer(theme.getButtonBackground(addElement));
                addElement.setClickListener((widget, mouseX, mouseY) -> {
                    this.addHandler.insertElement();
                    this.init(minecraft, width, height);
                });
                break;
            }
            int correct = errorOffset / (this.pageSize - j);
            errorOffset -= correct;
            offset += correct;
            ConfigValue<?> dummy = valueFactory.create(array.getId(), i);
            dummy.processFieldData(owner);
            Component label = this.getEntryLabel(dummy, i);
            ConfigEntryWidget widget = addRenderableWidget(new ConfigEntryWidget(30, viewportMin + 10 + j * 25 + offset, this.width - 60, 20, label, dummy, this.getConfigId(), this.theme));
            widget.setDescriptionRenderer(this);
            if (adapter == null) {
                Configuration.LOGGER.error(MARKER, "Missing display adapter for {} type, will not be displayed in GUI", compType.getSimpleName());
                continue;
            }
            try {
                adapter.placeWidgets(this.holder, dummy, owner, this.theme, widget);
            } catch (ClassCastException e) {
                Configuration.LOGGER.error(MARKER, new FormattedMessage("Unable to create config field for {}", compType.getSimpleName()), e);
            }
            if (!fixedSize) {
                final int elementIndex = i;
                ThemedButtonWidget removeButton = addRenderableWidget(new ThemedButtonWidget(this.width - 29, widget.getY(), 20, 20, CommonComponents.EMPTY, theme));
                removeButton.setClickListener((widget1, mouseX, mouseY) -> {
                    this.removeHandler.removeElementAt(elementIndex, (index1, src, dest) -> {
                        System.arraycopy(src, 0, dest, 0, index);
                        System.arraycopy(src, index + 1, dest, index, this.sizeSupplier.get() - 1 - index);
                        return dest;
                    });
                    this.init(minecraft, width, height);
                });
                removeButton.setBackgroundRenderer(theme.getButtonBackground(removeButton));
                removeButton.setForegroundRenderer(new TextureRenderer(REMOVE_ICON, 2, 2, 16, 16));
            }
        }
        addFooter();
    }

    private <T> Component getEntryLabel(ConfigValue<T> value, int index) {
        ValueData<T> valueData = value.getValueData();
        String languageKey = valueData.getLanguageKey(valueData.getAttributes()) + ".entry";
        Component translated = Component.translatable(languageKey);
        return Component.literal("[" + index + "] " + translated.getString());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics, mouseX, mouseY, partialTicks);
        ConfigTheme.Header themeHeader = this.theme.getHeader();
        ConfigTheme.Footer footer = this.theme.getFooter();
        Component headerLabel = themeHeader.customText() != null ? themeHeader.customText() : this.title;
        int titleWidth = this.font.width(headerLabel);
        graphics.drawString(font, headerLabel, (this.width - titleWidth) / 2, (HEADER_HEIGHT - this.font.lineHeight) / 2, themeHeader.foregroundColor());
        graphics.fill(0, 0, width, HEADER_HEIGHT, themeHeader.backgroundColor());
        graphics.fill(0, height - FOOTER_HEIGHT, width, height, footer.backgroundColor());
        Integer fillColor = this.theme.getBackgroundFillColor();
        if (fillColor != null) {
            graphics.fill(0, HEADER_HEIGHT, width, height - FOOTER_HEIGHT, fillColor);
        }
        renderables.forEach(renderable -> renderable.render(graphics, mouseX, mouseY, partialTicks));
        ConfigTheme.Scrollbar scrollbar = this.theme.getScrollbar();
        renderScrollbar(graphics, width - scrollbar.width(), HEADER_HEIGHT, scrollbar.width(), height - FOOTER_HEIGHT - HEADER_HEIGHT, index, this.getTotalSize(), pageSize, scrollbar.backgroundColor());
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amountX, double amountY) {
        int scale = (int) -amountY;
        int next = this.index + scale;
        if (next >= 0 && next + this.pageSize <= this.getTotalSize()) {
            this.index = next;
            this.init(minecraft, width, height);
            return true;
        }
        return false;
    }

    private int getTotalSize() {
        int elementSize = this.sizeSupplier.get();
        if (!this.fixedSize) {
            ++elementSize;
        }
        return elementSize;
    }

    public static <V> TypeAdapter.AdapterContext callbackCtx(Field parent, Class<V> componentType, BiConsumer<V, Integer> callback, int index) {
        return new DummyCallbackAdapter<>(componentType, parent, callback, index);
    }

    @FunctionalInterface
    public interface ElementAddHandler {
        void insertElement();
    }

    @FunctionalInterface
    public interface DummyConfigValueFactory {
        ConfigValue<?> create(String id, int elementIndex);
    }

    @FunctionalInterface
    public interface ElementRemoveHandler<V> {
        void removeElementAt(int index, ArrayTrimmer<V> trimmer);

        @FunctionalInterface
        interface ArrayTrimmer<V> {
            V trim(int index, V src, V dest);
        }
    }

    private static class DummyCallbackAdapter<V> implements TypeAdapter.AdapterContext {

        private final TypeAdapter<?> typeAdapter;
        private final Field parentField;
        private final BiConsumer<V, Integer> setCallback;
        private final int index;

        private DummyCallbackAdapter(Class<V> type, Field parentField, BiConsumer<V, Integer> setCallback, int index) {
            this.typeAdapter = TypeAdapterManager.forType(type).adapter();
            this.parentField = parentField;
            this.setCallback = setCallback;
            this.index = index;
        }

        @Override
        public TypeAdapter<?> getAdapter() {
            return typeAdapter;
        }

        @Override
        public Field getOwner() {
            return parentField;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void setFieldValue(Object value) {
            this.setCallback.accept((V) value, this.index);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void setValue(Object value) {
            this.setCallback.accept((V) value, this.index);
        }
    }
}
