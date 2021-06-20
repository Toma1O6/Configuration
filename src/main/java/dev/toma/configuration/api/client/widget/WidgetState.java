package dev.toma.configuration.api.client.widget;

public enum WidgetState {
    VISIBLE,
    DISABLED,
    HIDDEN;

    public boolean isEnabled() {
        return this == VISIBLE;
    }

    public boolean isDisabled() {
        return this == DISABLED;
    }

    public boolean isHidden() {
        return this == HIDDEN;
    }
}
