package dev.toma.configuration.config.adapter;

public record TypeAttributes<T>(TypeAdapter adapter, TypeMapper<T, Object> mapper) {
}
