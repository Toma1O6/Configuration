package dev.toma.configuration.api.client;

@Deprecated
public enum VerticalAlignment {

    TOP, CENTER, BOTTOM;

    public float getVerticalPos(int y, int height, int elementHeight) {
        switch (this) {
            case TOP:
                return y;
            case BOTTOM:
                return y + height - elementHeight;
            case CENTER:
            default:
                return y + (height - elementHeight) / 2f;
        }
    }
}
