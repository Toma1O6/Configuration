package dev.toma.configuration.api.client;

import dev.toma.configuration.api.type.CollectionType;
import dev.toma.configuration.api.type.ObjectType;

public interface IClientSettings {

    void setConfigScreenFactory(IScreenFactory<ObjectType> factory);

    void setConfigCollectionScreenFactory(IScreenFactory<CollectionType<?>> factory);

    void setBackgroundRenderer(IBackgroundRenderer backgroundRenderer);

    IWidgetManager getWidgetManager();

    IScreenFactory<ObjectType> getConfigScreenFactory();

    IScreenFactory<CollectionType<?>> getConfigCollectionScreenFactory();

    IBackgroundRenderer getBackgroundRenderer();
}
