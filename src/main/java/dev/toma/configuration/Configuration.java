package dev.toma.configuration;

import dev.toma.configuration.api.Config;
import dev.toma.configuration.api.ConfigPlugin;
import dev.toma.configuration.api.type.ObjectType;
import dev.toma.configuration.internal.ConfigHandler;
import dev.toma.configuration.client.screen.ConfigScreen;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import java.util.*;

@Mod(Configuration.MODID)
public class Configuration {

    public static final String MODID = "configuration";
    public static final Logger LOGGER = LogManager.getLogger("configs");
    private static final Map<String, ConfigPlugin> pluginMap = new HashMap<>();
    private static final Map<String, ObjectType> configMap = new HashMap<>();

    public Configuration() {
        loadPlugins();
        pluginMap.forEach((modid, plugin) -> {
            ObjectType type = ConfigHandler.loadConfig(plugin);
            if(type != null) {
                configMap.put(modid, type);
            }
        });
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
    }

    void setupClient(FMLClientSetupEvent event) {
        for (Map.Entry<String, ObjectType> entry : configMap.entrySet()) {
            String modid = entry.getKey();
            ObjectType type = entry.getValue();
            Optional<? extends ModContainer> container = ModList.get().getModContainerById(modid);
            container.ifPresent(mc -> mc.registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (minecraft, screen) -> new ConfigScreen(screen, type, modid)));
        }
    }

    public static ConfigPlugin getPlugin(String modID) {
        return pluginMap.get(modID);
    }

    public static ObjectType getConfig(String modID) {
        return configMap.get(modID);
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
}
