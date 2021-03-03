package dev.toma.configuration;

import dev.toma.configuration.api.Config;
import dev.toma.configuration.api.ConfigCreator;
import dev.toma.configuration.api.ConfigPlugin;
import dev.toma.configuration.api.type.IntType;
import dev.toma.configuration.api.type.ObjectType;
import dev.toma.configuration.api.util.NumberDisplayType;
import dev.toma.configuration.internal.ConfigHandler;
import dev.toma.configuration.internal.FileTracker;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import java.util.*;

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
@Mod(Configuration.MODID)
public class Configuration {

    public static final String MODID = "configuration";
    public static final Logger LOGGER = LogManager.getLogger("configs");
    protected static final Map<String, ConfigPlugin> pluginMap = new HashMap<>();
    protected static final Map<String, ObjectType> configMap = new HashMap<>();

    public Configuration() {
        loadPlugins();
        pluginMap.forEach((modid, plugin) -> {
            ObjectType type = ConfigHandler.loadConfig(plugin);
            if(type != null) {
                configMap.put(modid, type);
            }
        });
        FileTracker.INSTANCE.initialize();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
    }

    void setupClient(FMLClientSetupEvent event) {
        DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> ClientManager::setupPluginClient);
    }

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

    void loadPlugins() {
        List<ModFileScanData> scanDataList = ModList.get().getAllScanData();
        Set<String> classes = new LinkedHashSet<>();
        Type type = Type.getType(Config.class);
        for (ModFileScanData data : scanDataList) {
            Iterable<ModFileScanData.AnnotationData> annotationData = data.getAnnotations();
            for (ModFileScanData.AnnotationData annotation : annotationData) {
                if(Objects.equals(annotation.getAnnotationType(), type)) {
                    classes.add(annotation.getMemberName());
                }
            }
        }
        for (String classpath : classes) {
            try {
                Class<?> aClass = Class.forName(classpath);
                Class<? extends ConfigPlugin> instance = aClass.asSubclass(ConfigPlugin.class);
                ConfigPlugin plugin = instance.newInstance();
                pluginMap.put(plugin.getModID(), plugin);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | LinkageError e) {
                LOGGER.error("Failed to load {}", classpath, e);
            }
        }
    }

    //@Config
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
