package dev.toma.configuration.internal.proxy;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.api.Config;
import dev.toma.configuration.api.ConfigPlugin;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Set;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        ASMDataTable table = event.getAsmData();
        String annotation = Config.class.getCanonicalName();
        Set<ASMDataTable.ASMData> dataSet = table.getAll(annotation);
        for (ASMDataTable.ASMData data : dataSet) {
            try {
                Class<?> cls = Class.forName(data.getClassName());
                Class<? extends ConfigPlugin> cfgClass = cls.asSubclass(ConfigPlugin.class);
                ConfigPlugin instance = cfgClass.newInstance();
                Configuration.getPluginMap().put(instance.getModID(), instance);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | LinkageError e) {
                Configuration.LOGGER.error("Failed to load {}", data.getClassName(), e);
            }
        }
    }
}
