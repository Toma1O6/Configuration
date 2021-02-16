package dev.toma.configuration;

import dev.toma.configuration.api.Config;
import dev.toma.configuration.api.ConfigCreator;
import dev.toma.configuration.api.ConfigPlugin;
import dev.toma.configuration.api.type.IntType;
import dev.toma.configuration.api.type.ObjectType;
import dev.toma.configuration.api.util.NumberDisplayType;
import dev.toma.configuration.internal.ConfigHandler;
import dev.toma.configuration.internal.FileTracker;
import dev.toma.configuration.internal.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Configuration's main class <p>
 *
 * How to create your own config with this API:
 * 1) Create your main config file
 * 2) Annotate your class with {@link Config}
 * 3) Implement {@link ConfigPlugin} interface and it's required methods
 * 4) Well done, you have created your config file
 * <p>
 * This class also provides few methods you might like: <p>
 * {@link Configuration#getPlugin(String)} <p> {@link Configuration#getConfig(String)}
 *
 * @author Toma
 */
@Mod(modid = Configuration.MODID, name = "Configuration", version = "1.0.2", acceptedMinecraftVersions = "1.12.2", updateJSON = "https://raw.githubusercontent.com/Toma1O6/Configuration/master/versions.json")
public class Configuration {

    public static final String MODID = "configuration";
    public static final Logger LOGGER = LogManager.getLogger("configs");
    protected static final Map<String, ConfigPlugin> pluginMap = new HashMap<>();
    protected static final Map<String, ObjectType> configMap = new HashMap<>();
    @SidedProxy(clientSide = "dev.toma.configuration.internal.proxy.ClientProxy", serverSide = "dev.toma.configuration.internal.proxy.ServerProxy")
    public static CommonProxy proxy;

    /**
     * @param modID ID of very specific mod
     * @return {@link Optional} object possibly containing {@link ConfigPlugin} for specified modID
     */
    public synchronized static Optional<ConfigPlugin> getPlugin(String modID) {
        return Optional.ofNullable(pluginMap.get(modID));
    }

    /**
     * @param modID ID of very specific mod
     * @return {@link Optional} object possibly containing {@link ObjectType} for specified modID
     */
    public synchronized static Optional<ObjectType> getConfig(String modID) {
        return Optional.ofNullable(configMap.get(modID));
    }

    public static Map<String, ConfigPlugin> getPluginMap() {
        return pluginMap;
    }

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        pluginMap.forEach((modid, plugin) -> {
            ObjectType type = ConfigHandler.loadConfig(plugin);
            if(type != null) {
                configMap.put(modid, type);
            }
        });
        FileTracker.INSTANCE.initialize();
    }

    @Config
    public static class InternalConfig implements ConfigPlugin {

        public static IntType fileCheckTimer;

        @Override
        public String getModID() {
            return MODID;
        }

        @Override
        public void buildConfigStructure(ConfigCreator builder) {
            fileCheckTimer = builder.createInt(
                    "File Check Timer",
                    5,
                    0,
                    60,
                    "Set timer for checking config file changes", "Unit: seconds", "Set to 0 to disable file checks"
            ).setDisplay(NumberDisplayType.TEXT_FIELD_SLIDER);
        }
    }
}
