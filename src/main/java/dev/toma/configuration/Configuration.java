package dev.toma.configuration;

import dev.toma.configuration.api.Config;
import dev.toma.configuration.api.IConfigPlugin;
import dev.toma.configuration.api.ModConfig;
import dev.toma.configuration.api.type.ObjectType;
import dev.toma.configuration.internal.ConfigHandler;
import dev.toma.configuration.internal.FileTracker;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
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
 * 3) Implement {@link IConfigPlugin} interface and it's required methods
 * 4) Well done, you have created your config file
 * <p>
 * This class also provides special method you might like: <p>
 * {@link Configuration#getConfig(String)}
 *
 * @author Toma
 */
@Mod(Configuration.MODID)
public class Configuration {

    public static final String MODID = "configuration";
    public static final Logger LOGGER = LogManager.getLogger("configs");
    private static final IDistHandler distHandler = DistExecutor.safeRunForDist(() -> DistHandlerClient::new, () -> DistHandlerServer::new);
    private static final Map<String, ModConfig> configMap = new HashMap<>();

    public Configuration() {
        init();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
    }

    void setupClient(FMLClientSetupEvent event) {
        synchronized (configMap) {
            for (ModConfig config : configMap.values()) {
                distHandler.runConfigSetup(config);
            }
        }
    }

    public static void loadConfig(IConfigPlugin plugin) {
        ModConfig config = ConfigHandler.loadModConfig(plugin);
        if (config != null) {
            configMap.put(plugin.getModID(), config);
            FileTracker.INSTANCE.registerConfigTrackingEntry(config);
        }
    }

    /**
     * @param modID ID of very specific mod
     * @return {@link Optional} object possibly containing {@link ObjectType} for specified modID
     */
    public static Optional<ModConfig> getConfig(String modID) {
        synchronized (configMap) {
            return Optional.ofNullable(configMap.get(modID));
        }
    }

    private synchronized void init() {
        List<IConfigPlugin> loadedPlugins = new ArrayList<>();
        loadPlugins(loadedPlugins);

        for (IConfigPlugin plugin : loadedPlugins) {
            loadConfig(plugin);
        }
        FileTracker.INSTANCE.initialize(configMap.values());
    }

    private void loadPlugins(List<IConfigPlugin> pluginList) {
        List<ModFileScanData> scanDataList = ModList.get().getAllScanData();
        Set<String> classes = new LinkedHashSet<>();
        Type type = Type.getType(Config.class);
        for (ModFileScanData data : scanDataList) {
            Iterable<ModFileScanData.AnnotationData> annotationData = data.getAnnotations();
            for (ModFileScanData.AnnotationData annotation : annotationData) {
                if(Objects.equals(annotation.annotationType(), type)) {
                    classes.add(annotation.memberName());
                }
            }
        }
        for (String classpath : classes) {
            try {
                Class<?> aClass = Class.forName(classpath);
                Class<? extends IConfigPlugin> instance = aClass.asSubclass(IConfigPlugin.class);
                IConfigPlugin plugin = instance.newInstance();
                if (!plugin.getModID().equals(MODID) || !FMLEnvironment.production)
                    pluginList.add(plugin);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | LinkageError e) {
                LOGGER.error("Failed to load {}", classpath, e);
            }
        }
    }
}
