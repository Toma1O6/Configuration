package dev.toma.configuration.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.client.widget.ConfigEntryWidget;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.io.ConfigIO;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.ObjectValue;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Collection;

public abstract class AbstractConfigScreen extends Screen {

    public static final int HEADER_HEIGHT = 35;
    public static final int FOOTER_HEIGHT = 30;
    public static final Marker MARKER = MarkerManager.getMarker("Screen");
    protected final Screen last;
    protected final String configId;

    protected int index;
    protected int pageSize;

    public AbstractConfigScreen(ITextComponent title, Screen previous, String configId) {
        super(title);
        this.last = previous;
        this.configId = configId;
    }

    @Override
    public void onClose() {
        super.onClose();
        this.saveConfig(true);
    }

    public static void renderScrollbar(MatrixStack stack, int x, int y, int width, int height, int index, int valueCount, int paging) {
        if (valueCount <= paging)
            return;
        double step = height / (double) valueCount;
        int min = MathHelper.floor(index * step);
        int max = MathHelper.ceil((index + paging) * step);
        int y1 = y + min;
        int y2 = y + max;
        fill(stack, x, y, x + width, y + height, 0xFF << 24);

        fill(stack, x, y1, x + width, y2, 0xFF888888);
        fill(stack, x, y1, x + width - 1, y2 - 1, 0xFFEEEEEE);
        fill(stack, x + 1, y1 + 1, x + width - 1, y2 - 1, 0xFFCCCCCC);
    }

    protected void addFooter() {
        int centerY = this.height - FOOTER_HEIGHT + (FOOTER_HEIGHT - 20) / 2;
        addButton(new Button(20, centerY, 50, 20, ConfigEntryWidget.BACK, this::buttonBackClicked));
        addButton(new Button(75, centerY, 120, 20, ConfigEntryWidget.REVERT_DEFAULTS, this::buttonRevertToDefaultClicked));
        addButton(new Button(200, centerY, 120, 20, ConfigEntryWidget.REVERT_CHANGES, this::buttonRevertChangesClicked));
    }

    protected void correctScrollingIndex(int count) {
        if (index + pageSize > count) {
            index = Math.max(count - pageSize, 0);
        }
    }

    protected Screen getFirstNonConfigScreen() {
        Screen screen = last;
        while (screen instanceof ConfigScreen) {
            screen = ((ConfigScreen) screen).last;
        }
        return screen;
    }

    private void buttonBackClicked(Button button) {
        this.minecraft.setScreen(this.last);
        this.saveConfig();
    }

    private void buttonRevertToDefaultClicked(Button button) {
        Configuration.LOGGER.info(MARKER, "Reverting config {} to default values", this.configId);
        ConfigHolder.getConfig(this.configId).ifPresent(holder -> {
            revertToDefault(holder.values());
            ConfigIO.saveClientValues(holder);
        });
        this.backToConfigList();
    }

    private void buttonRevertChangesClicked(Button button) {
        ConfigHolder.getConfig(this.configId).ifPresent(ConfigIO::reloadClientValues);
        this.backToConfigList();
    }

    private void revertToDefault(Collection<ConfigValue<?>> configValues) {
        configValues.forEach(val -> {
            if (val instanceof ObjectValue) {
                ObjectValue objVal = (ObjectValue) val;
                this.revertToDefault(objVal.get().values());
            } else {
                val.useDefaultValue();
            }
        });
    }

    private void backToConfigList() {
        this.minecraft.setScreen(this.getFirstNonConfigScreen());
        this.saveConfig();
    }

    private void saveConfig() {
        saveConfig(false);
    }

    private void saveConfig(boolean force) {
        if (force || !(last instanceof AbstractConfigScreen)) {
            ConfigHolder.getConfig(this.configId).ifPresent(ConfigIO::saveClientValues);
        }
    }
}
