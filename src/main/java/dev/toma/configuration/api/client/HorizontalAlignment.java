package dev.toma.configuration.api.client;

@Deprecated
public enum HorizontalAlignment {

    LEFT, CENTER, RIGHT;

    public float getHorizontalPos(int x, int width, int elementWidth) {
        switch (this) {
            case LEFT:
                return x;
            case RIGHT:
                return x + width - elementWidth;
            case CENTER:
            default:
                return x + (width - elementWidth) / 2f;
        }
    }
}
