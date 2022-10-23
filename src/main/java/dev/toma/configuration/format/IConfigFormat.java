package dev.toma.configuration.format;

import dev.toma.configuration.value.ConfigValue;
import dev.toma.configuration.exception.ConfigLoadingException;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public interface IConfigFormat {

    Set<ConfigValue<?>> processFile(ResourceLocation config, File file) throws IOException, ConfigLoadingException; // TODO possibly just expose reader directly

    String fileExtension();
}
