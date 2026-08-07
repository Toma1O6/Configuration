package dev.toma.configuration.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.client.ConfigurationClient;
import dev.toma.configuration.config.ConfigHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModMenuCompatibility implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        List<ConfigHolder<?>> configs = ConfigHolder.getConfigsByGroup(Configuration.MODID);
        if (configs.size() > 1) {
            return screen -> ConfigurationClient.getConfigScreenByGroup(configs, Configuration.MODID, screen);
        } else {
            return screen -> ConfigurationClient.getConfigScreen(Configuration.MODID, screen);
        }
    }

    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        Map<String, ConfigScreenFactory<?>> map = new HashMap<>();
        Map<String, List<ConfigHolder<?>>> byGroup = ConfigHolder.getConfigGroupingByGroup();
        for (Map.Entry<String, List<ConfigHolder<?>>> entry : byGroup.entrySet()) {
            String group = entry.getKey();
            List<ConfigHolder<?>> configHolders = entry.getValue();
            ConfigScreenFactory<?> factory = parent -> {
                int i = configHolders.size();
                if (i > 1) {
                    return ConfigurationClient.getConfigScreenByGroup(configHolders, group, parent);
                } else if (i == 1) {
                    return ConfigurationClient.getConfigScreen(configHolders.getFirst().getConfigId(), parent);
                }
                return null;
            };
            map.put(group, factory);
        }
        return map;
    }
}
