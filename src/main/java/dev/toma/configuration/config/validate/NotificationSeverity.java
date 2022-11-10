package dev.toma.configuration.config.validate;

import dev.toma.configuration.Configuration;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public enum NotificationSeverity {

    INFO("", TextFormatting.RESET),
    WARNING("warning", TextFormatting.GOLD),
    ERROR("error", TextFormatting.RED);

    private final ResourceLocation icon;
    private final TextFormatting extraFormatting;

    NotificationSeverity(String iconName, TextFormatting formatting) {
        this.icon = new ResourceLocation(Configuration.MODID, "textures/icons/" + iconName + ".png");
        this.extraFormatting = formatting;
    }

    public ResourceLocation getIcon() {
        return icon;
    }

    public TextFormatting getExtraFormatting() {
        return extraFormatting;
    }

    public boolean isOkStatus() {
        return this == INFO;
    }
}
