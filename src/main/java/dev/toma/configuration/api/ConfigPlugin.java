package dev.toma.configuration.api;

import dev.toma.configuration.api.type.ObjectType;
import dev.toma.configuration.internal.DefaultConfigCreatorImpl;

public interface ConfigPlugin {

    String getModID();

    void buildConfigStructure(ConfigCreator builder);

    default ConfigCreator builder(ObjectType configObject) {
        DefaultConfigCreatorImpl configCreator = new DefaultConfigCreatorImpl();
        configCreator.assignTo(configObject);
        return configCreator;
    }
}
