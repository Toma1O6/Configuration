package dev.toma.configuration.api.client.component;

import dev.toma.configuration.api.type.AbstractConfigType;

public interface IComponentListener<T extends AbstractConfigType<?>> {

    void onChange(T t);
}
