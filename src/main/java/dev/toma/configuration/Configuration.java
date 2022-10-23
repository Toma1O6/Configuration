package dev.toma.configuration;

import dev.toma.configuration.annotation.Config;
import dev.toma.configuration.config.PrimitivesTest;
import dev.toma.configuration.format.ConfigFormats;
import dev.toma.configuration.format.IConfigFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Mod(Configuration.MODID)
public final class Configuration {

    public static final String MODID = "configuration";
    public static final Logger LOGGER = LogManager.getLogger("Configuration");
    private static final Map<String, Map<String, ConfigHolder<?>>> CONFIGS_BY_MODID = new HashMap<>();

    public Configuration() {
        ConfigHolder<PrimitivesTest> holder = registerConfig(PrimitivesTest.class, ConfigFormats.JSON4);
        System.out.println(holder);
    }

    public static <T> ConfigHolder<T> registerConfig(Class<T> configClass, IConfigFormat format) {
        try {
            T configInstance = configClass.getDeclaredConstructor().newInstance();
            Config configDef = configClass.getAnnotation(Config.class);
            if (configDef == null) {
                LOGGER.error("Attempted to register non-config class {}. Use @Config annotation for configuration classes", configClass);
                return null;
            }
            String modId = configDef.value();
            String filename = configDef.filename().isEmpty() ? modId : configDef.filename();
            ResourceLocation configId = new ResourceLocation(modId, filename);
            ConfigHolder<T> holder = new ConfigHolder<>(configId, configInstance, format);
            Map<String, ConfigHolder<?>> map = CONFIGS_BY_MODID.computeIfAbsent(modId, key -> new HashMap<>());
            map.put(filename, holder);
            return holder;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            LOGGER.fatal("Failed to instantiate configuration of class {} due to {} exception", configClass, e);
            throw new RuntimeException(e);
        }
    }
}
