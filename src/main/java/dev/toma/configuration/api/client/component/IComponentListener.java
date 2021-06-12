package dev.toma.configuration.api.client.component;

import dev.toma.configuration.api.IConfigType;

public interface IComponentListener<T extends IConfigType<?>> {

    void onChange(T t);
}
