package dev.toma.configuration;

import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.format.ConfigFormats;
import dev.toma.configuration.config.format.IConfigFormatHandler;
import dev.toma.configuration.config.format.PropertiesFormat;
import dev.toma.configuration.config.io.ConfigIO;
import dev.toma.configuration.config.test.JsonConfig;
import dev.toma.configuration.config.test.PropertiesConfig;
import dev.toma.configuration.network.Networking;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

@Mod(Configuration.MODID)
public final class Configuration {

    public static final String MODID = "configuration";
    public static final Logger LOGGER = LogManager.getLogger("Configuration");
    public static final Marker MAIN_MARKER = MarkerManager.getMarker("main");
    public static JsonConfig jsonConfig;
    public static PropertiesConfig propertiesConfig;

    public Configuration() {
        jsonConfig = registerConfig(JsonConfig.class, ConfigFormats.json()).getConfigInstance();
        propertiesConfig = registerConfig(PropertiesConfig.class, ConfigFormats.properties(new PropertiesFormat.Settings().newlines(1))).getConfigInstance();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
    }

    public static <CFG> ConfigHolder<CFG> registerConfig(Class<CFG> cfgClass, IConfigFormatHandler formatFactory) {
        Config cfg = cfgClass.getAnnotation(Config.class);
        if (cfg == null) {
            throw new IllegalArgumentException("Config class must be annotated with '@Config' annotation");
        }
        String id = cfg.id();
        String filename = cfg.filename();
        if (filename.isEmpty()) {
            filename = id;
        }
        ConfigHolder<CFG> holder = new ConfigHolder<>(cfgClass, id, filename, formatFactory);
        ConfigHolder.registerConfig(holder);
        if (cfgClass.getAnnotation(Config.NoAutoSync.class) == null) {
            ConfigIO.FILE_WATCH_MANAGER.addTrackedConfig(holder);
        }
        return holder;
    }

    private void init(FMLCommonSetupEvent event) {
        Networking.PacketRegistry.register();
        ConfigIO.FILE_WATCH_MANAGER.startService();
    }
}
