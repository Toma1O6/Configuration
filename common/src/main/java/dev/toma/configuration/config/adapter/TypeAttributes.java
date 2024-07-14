package dev.toma.configuration.config.adapter;

public record TypeAttributes<T>(TypeAdapter<T> adapter, TypeMapper<T, Object> mapper) {
}
