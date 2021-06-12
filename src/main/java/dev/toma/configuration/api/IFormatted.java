package dev.toma.configuration.api;

public interface IFormatted<N extends Number> {

    String getFormatted();

    String formatNumber(N num);
}
