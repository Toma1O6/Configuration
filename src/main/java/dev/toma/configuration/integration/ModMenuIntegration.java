package dev.toma.configuration.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.TestingConfig;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> Configuration.getConfigScreen(TestingConfig.class, parent);
    }
}
