package dev.toma.configuration.api;

import dev.toma.configuration.api.client.IClientSettings;
import dev.toma.configuration.api.client.IModID;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Basic config element. Your main config class must
 * implement this otherwise it won't be found
 *
 * @author Toma
 */
public interface IConfigPlugin extends IModID {

    /**
     * Create your config structure here.
     * You must use the supplied {@link IConfigWriter} to construct your config objects
     *
     * @param writer The config writer
     */
    void buildConfig(IConfigWriter writer);

    /**
     * You can change your config's file name
     * @return Name for your config file
     */
    default String getConfigFileName() {
        return this.getModID();
    }

    /**
     * You can change all client related stuff in this method.
     * Unsafe for server, check which side you're on!
     * @param settings Settings API
     */
    @OnlyIn(Dist.CLIENT)
    default void setupClient(IClientSettings settings) {}
}
