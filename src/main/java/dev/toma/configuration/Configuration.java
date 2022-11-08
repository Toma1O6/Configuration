package dev.toma.configuration;

import dev.toma.configuration.client.screen.ConfigGroupScreen;
import dev.toma.configuration.client.screen.ConfigScreen;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.format.ConfigFormats;
import dev.toma.configuration.config.format.IConfigFormatHandler;
import dev.toma.configuration.config.format.PropertiesFormat;
import dev.toma.configuration.config.io.ConfigIO;
import dev.toma.configuration.config.test.JsonConfig;
import dev.toma.configuration.config.test.PropertiesConfig;
import dev.toma.configuration.config.test.YamlConfig;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.network.Networking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mod(Configuration.MODID)
public final class Configuration {

    public static final String MODID = "configuration";
    public static final Logger LOGGER = LogManager.getLogger("Configuration");
    public static final Marker MAIN_MARKER = MarkerManager.getMarker("main");
    public static JsonConfig jsonConfig;
    public static YamlConfig yamlConfig;
    public static PropertiesConfig propertiesConfig;

    public Configuration() {
        jsonConfig = registerConfig(JsonConfig.class, ConfigFormats.json()).getConfigInstance();
        yamlConfig = registerConfig(YamlConfig.class, ConfigFormats.yaml()).getConfigInstance();
        propertiesConfig = registerConfig(PropertiesConfig.class, ConfigFormats.properties(new PropertiesFormat.Settings().newlines(1))).getConfigInstance();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::init);
        modEventBus.addListener(this::clientInit);
    }

    /**
     * Registers your config class. Config will be immediately loaded upon calling.
     *
     * @param cfgClass Your config class
     * @param formatFactory File format to be used by this config class. You can use values
     *                      from {@link ConfigFormats} for example.
     * @return Config holder containing your config instance. You obtain it by calling
     * {@link ConfigHolder#getConfigInstance()} method.
     * @param <CFG> Config type
     */
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
        String group = cfg.group();
        if (group.isEmpty()) {
            group = id;
        }
        ConfigHolder<CFG> holder = new ConfigHolder<>(cfgClass, id, filename, group, formatFactory);
        ConfigHolder.registerConfig(holder);
        if (cfgClass.getAnnotation(Config.NoAutoSync.class) == null) {
            ConfigIO.FILE_WATCH_MANAGER.addTrackedConfig(holder);
        }
        return holder;
    }

    @OnlyIn(Dist.CLIENT)
    public static Screen getConfigScreen(Class<?> configClass, Screen previous) {
        Config cfg = configClass.getAnnotation(Config.class);
        if (cfg == null) {
            return null;
        }
        String id = cfg.id();
        return getConfigScreen(id, previous);
    }

    @OnlyIn(Dist.CLIENT)
    public static Screen getConfigScreen(String configId, Screen previous) {
        return ConfigHolder.getConfig(configId).map(holder -> {
            Map<String, ConfigValue<?>> valueMap = holder.getValueMap();
            return new ConfigScreen(configId, holder.getConfigId(), valueMap, previous);
        }).orElse(null);
    }

    @OnlyIn(Dist.CLIENT)
    public static Screen getConfigScreenByGroup(String group, Screen previous) {
        List<ConfigHolder<?>> list = ConfigHolder.getConfigsByGroup(group);
        if (list.isEmpty())
            return null;
        return getConfigScreenByGroup(list, group, previous);
    }

    @OnlyIn(Dist.CLIENT)
    public static Screen getConfigScreenByGroup(List<ConfigHolder<?>> group, String groupId, Screen previous) {
        return new ConfigGroupScreen(previous, groupId, group);
    }

    private void init(FMLCommonSetupEvent event) {
        Networking.PacketRegistry.register();
        ConfigIO.FILE_WATCH_MANAGER.startService();
    }

    private void clientInit(FMLClientSetupEvent event) {
        Map<String, List<ConfigHolder<?>>> groups = ConfigHolder.getConfigGroupingByGroup();
        ModList modList = ModList.get();
        for (Map.Entry<String, List<ConfigHolder<?>>> entry : groups.entrySet()) {
            String modId = entry.getKey();
            Optional<? extends ModContainer> optional = modList.getModContainerById(modId);
            optional.ifPresent(modContainer -> {
                List<ConfigHolder<?>> list = entry.getValue();
                modContainer.registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (client, previousScreen) -> {
                    if (list.size() == 1) {
                        return getConfigScreen(modId, previousScreen);
                    }
                    return getConfigScreenByGroup(list, modId, previousScreen);
                });
            });
        }
    }
}
