package dev.toma.configuration.api;

import dev.toma.configuration.api.client.BackgroundRenderer;
import dev.toma.configuration.api.type.ObjectType;
import dev.toma.configuration.internal.DefaultConfigCreatorImpl;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Basic config element. Your main config class must
 * implement this otherwise it won't be found
 *
 * @author Toma
 */
public interface ConfigPlugin {

    /**
     * Function to match plugin to specific mod
     * @return Your mod ID
     */
    String getModID();

    /**
     * Construct your config structure here
     * Every element you add using the {@link ConfigCreator}
     * will be automatically added into your config
     *
     * @param builder The config builder
     */
    void buildConfigStructure(ConfigCreator builder);

    /**
     * You can change your config's file name
     * @return Name for your config file
     */
    default String getConfigFileName() {
        return this.getModID();
    }

    /**
     * This allows you to create your own implementations of
     * {@link ConfigCreator} if you feel like you need one
     *
     * @param configObject Object to be constructed
     * @return Instance of {@link ConfigCreator}
     */
    default ConfigCreator builder(ObjectType configObject) {
        DefaultConfigCreatorImpl configCreator = new DefaultConfigCreatorImpl();
        configCreator.assignTo(configObject);
        return configCreator;
    }

    /**
     * Background renderer is responsible for rendering UI background.
     * Default is dirt background as seen in vanilla menus
     *
     * @return your implementation
     */
    @OnlyIn(Dist.CLIENT)
    default BackgroundRenderer getBackgroundRenderer() {
        return BackgroundRenderer.DirtBackground.INSTANCE;
    }
}
