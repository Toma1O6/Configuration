package dev.toma.configuration.client;

import dev.toma.configuration.api.client.IBackgroundRenderer;
import dev.toma.configuration.api.client.IClientSettings;
import dev.toma.configuration.api.client.IScreenFactory;
import dev.toma.configuration.api.client.IWidgetManager;
import dev.toma.configuration.api.client.screen.ConfigCollectionScreen;
import dev.toma.configuration.api.client.screen.ModConfigScreen;
import dev.toma.configuration.api.client.widget.WidgetType;
import dev.toma.configuration.api.type.CollectionType;
import dev.toma.configuration.api.type.ObjectType;

import java.util.Objects;

public class ClientSettings implements IClientSettings {

    private IScreenFactory<ObjectType> configScreenFactory;
    private IScreenFactory<CollectionType<?>> collectionScreenFactory;
    private IBackgroundRenderer backgroundRenderer;
    private final IWidgetManager widgetManager;

    public ClientSettings() {
        setConfigScreenFactory(ModConfigScreen::new);
        setConfigCollectionScreenFactory(ConfigCollectionScreen::new);
        setBackgroundRenderer(new BackgroundRenderer());
        widgetManager = new WidgetManager();

        widgetManager.setStyle(WidgetType.LABEL, widget -> widget.foreground = getBackgroundRenderer().getDefaultLabelForegroundColor(), null);
        widgetManager.setStyle(WidgetType.INT_SLIDER, widget -> widget.showValue = false, "hideValue");
        widgetManager.setStyle(WidgetType.DOUBLE_SLIDER, widget -> widget.showValue = false, "hideValue");
    }

    @Override
    public void setConfigScreenFactory(IScreenFactory<ObjectType> factory) {
        configScreenFactory = Objects.requireNonNull(factory);
    }

    @Override
    public void setConfigCollectionScreenFactory(IScreenFactory<CollectionType<?>> factory) {
        collectionScreenFactory = factory;
    }

    @Override
    public void setBackgroundRenderer(IBackgroundRenderer backgroundRenderer) {
        this.backgroundRenderer = backgroundRenderer;
    }

    @Override
    public IWidgetManager getWidgetManager() {
        return widgetManager;
    }

    @Override
    public IScreenFactory<ObjectType> getConfigScreenFactory() {
        return configScreenFactory;
    }

    @Override
    public IScreenFactory<CollectionType<?>> getConfigCollectionScreenFactory() {
        return collectionScreenFactory;
    }

    @Override
    public IBackgroundRenderer getBackgroundRenderer() {
        return backgroundRenderer;
    }
}
