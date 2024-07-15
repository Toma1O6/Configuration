package dev.toma.configuration.client.screen;

public final class WidgetPlacerHelper {

    public static int getLeft(int x, int totalWidth) {
        return x + totalWidth - getWidth(totalWidth) - 42;
    }

    public static int getWidth(int totalWidth) {
        return (int) (totalWidth / 3.5);
    }
}
