package dev.toma.configuration.api.client;

import dev.toma.configuration.api.type.CollectionType;
import dev.toma.configuration.api.type.ObjectType;

/**
 * Customizable client settings.
 * Register all custom screen factories/widgets/placers and styles here.
 */
public interface IClientSettings {

    /**
     * Sets config factory for objects/subcategories
     * @param factory Screen factory
     */
    void setConfigScreenFactory(IScreenFactory<ObjectType> factory);

    /**
     * Sets config factory for collections
     * @param factory Screen factory
     */
    void setConfigCollectionScreenFactory(IScreenFactory<CollectionType<?>> factory);

    /**
     * Sets background renderer for this screens
     * @param backgroundRenderer Background renderer implementation
     */
    void setBackgroundRenderer(IBackgroundRenderer backgroundRenderer);

    /**
     * WidgetManager is API for complete widget control
     * @return IWidgetManager implementation
     */
    @Deprecated
    IWidgetManager getWidgetManager();

    // ================= INTERNAL STUFF

    IScreenFactory<ObjectType> getConfigScreenFactory();
    IScreenFactory<CollectionType<?>> getConfigCollectionScreenFactory();
    IBackgroundRenderer getBackgroundRenderer();
}
