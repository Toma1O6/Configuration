package dev.toma.configuration.internal;

public interface Formatting<T extends Number> {

    String getFormatted();

    String formatNumber(T num);
}
