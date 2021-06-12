package dev.toma.configuration.internal;

import com.google.gson.*;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.api.IConfigPlugin;
import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.IConfigWriter;
import dev.toma.configuration.api.type.ObjectType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ConfigHandler {

    static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    public static final Gson GSON_OUT = new GsonBuilder().create();

    public static ObjectType loadConfig(IConfigPlugin plugin) {
        File configDir = new File(".", "config");
        if (!configDir.exists() || !configDir.isDirectory()) {
            Configuration.LOGGER.fatal("Couldn't locate config directory at {}", configDir.getAbsolutePath());
            return null;
        }
        File jsonFile = new File(configDir, plugin.getConfigFileName() + ".json");
        ObjectType configObject = new ObjectType(new ObjectSpec(plugin.getModID(), new ConfigWriter()));
        if (!jsonFile.exists()) {
            createDefaultConfigFile(jsonFile, plugin, configObject);
        } else {
            try {
                loadData(plugin, configObject, jsonFile);
            } catch (FileNotFoundException ex) {
                Configuration.LOGGER.error(ex.toString());
                return null;
            } catch (JsonParseException exception) {
                Configuration.LOGGER.error("Exception parsing {}'s config, new one will be generated", plugin.getModID());
                createDefaultConfigFile(jsonFile, plugin, configObject);
            } catch (Exception e) {
                Configuration.LOGGER.error("Unexpected error occurred while parsing {}'s config: {}", plugin.getModID(), e.toString());
                return null;
            }
        }
        writeData(configObject, jsonFile, false);
        return configObject;
    }

    public static void createDefaultConfigFile(File file, IConfigPlugin plugin, ObjectType type) {
        try {
            file.createNewFile();
            IConfigWriter writer = new ConfigWriter();
            writer.setWritingObject(type);
            plugin.buildConfig(writer);
        } catch (Exception exception) {
            Configuration.LOGGER.error("Error loading file: {} from {}. Reason: {}", file.getAbsolutePath(), plugin.getModID(), exception.toString());
        }
    }

    public static void loadData(IConfigPlugin plugin, ObjectType type, File jsonFile) throws JsonParseException, FileNotFoundException {
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8));;
        if(!element.isJsonObject()) {
            throw new JsonParseException("Found corrupted config file for " + plugin.getModID() + " plugin");
        }
        JsonObject savedData = element.getAsJsonObject();
        IConfigWriter writer = new ConfigWriter();
        writer.setWritingObject(type);
        plugin.buildConfig(writer);
        Map<String, IConfigType<?>> map = type.get();
        for (Map.Entry<String, IConfigType<?>> entry : map.entrySet()) {
            if (savedData.has(entry.getKey())) {
                entry.getValue().loadData(savedData.getAsJsonObject(entry.getKey()));
            }
        }
    }

    public static void writeData(ObjectType type, File file, boolean isUpdate) {
        try {
            Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            JsonObject object = new JsonObject();
            for (Map.Entry<String, IConfigType<?>> entry : type.get().entrySet()) {
                IConfigType<?> configType = entry.getValue();
                if(!isUpdate)
                    configType.generateComments();
                configType.saveData(object, isUpdate);
            }
            writer.write(GSON.toJson(object));
            writer.close();
        } catch (Exception e) {
            Configuration.LOGGER.error("Error writing file: {}, reason: {}", file.getAbsolutePath(), e.toString());
        }
    }

    public synchronized static void write(IConfigPlugin plugin, ObjectType type, FileTracker.Entry entry) {
        File configDir = new File(".", "config");
        if (!configDir.exists() || !configDir.isDirectory()) {
            Configuration.LOGGER.fatal("Couldn't locate config directory at {}", configDir.getAbsolutePath());
            return;
        }
        File jsonFile = new File(configDir, plugin.getConfigFileName() + ".json");
        if (!jsonFile.exists()) {
            return;
        }
        writeData(type, jsonFile, true);
        entry.modified = jsonFile.lastModified();
    }
}
