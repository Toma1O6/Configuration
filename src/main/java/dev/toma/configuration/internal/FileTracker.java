package dev.toma.configuration.internal;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.api.IConfigPlugin;
import dev.toma.configuration.api.ModConfig;
import dev.toma.configuration.api.client.IModID;
import dev.toma.configuration.exception.ConfigLoadDataException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FileTracker {

    public static final FileTracker INSTANCE = new FileTracker();
    private static final File CONFIG_DIR = new File(".", "config");
    private final Logger logger = LogManager.getLogger("Configuration");
    private final Marker marker = MarkerManager.getMarker("FileTracker");
    private final Map<String, Entry> entryList = new HashMap<>();
    private final Set<String> ourPaths = new HashSet<>();
    private final Set<String> expectedChanges = new HashSet<>();
    private final WatchService watchService;
    private final ScheduledExecutorService asyncService = Executors.newSingleThreadScheduledExecutor();

    public FileTracker() {
        WatchService service = null;
        try {
            service = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            logger.fatal(marker, "!! Watch service create failed, files will not be synchronized automatically !!");
            logger.fatal(marker, e.toString());
        } finally {
            this.watchService = service;
        }
    }

    public void initialize(Collection<ModConfig> configs) {
        if(!CONFIG_DIR.exists()) {
            CONFIG_DIR.mkdirs();
        }
        if(!CONFIG_DIR.isDirectory()) {
            throw new IllegalStateException("Config file must be a directory!");
        }
        configs.forEach(this::registerConfigTrackingEntry);
        initializeWatchService();
    }

    public void registerConfigTrackingEntry(ModConfig config) {
        IConfigPlugin plugin = config.getPlugin();
        File configFile = new File(CONFIG_DIR, plugin.getConfigFileName() + ".json");
        if (!configFile.exists()) {
            logger.error(marker, "Couldn't locate config file {}, excluding {} from FileTracker", configFile.getAbsolutePath(), plugin.getModID());
            return;
        }
        entryList.put(plugin.getModID(), new Entry(config, configFile.getName()));
        ourPaths.add(configFile.getName());
        logger.info(marker, "Added {} plugin into FileTracker", plugin.getModID());
    }

    public void runConfigUpdateAsync(IModID modIdProvider, UpdateAction action) {
        String modId = modIdProvider.getModID();
        Entry entry = entryList.get(modId);
        if (entry == null) {
            logger.error(marker, "Attempted to update config file, but no entry was registered for " + modId);
            return;
        }
        CompletableFuture
                .runAsync(() -> runUpdate(entry, action))
                .exceptionally(throwable -> {
                    logger.error(marker, "Config update failed for {}, {}", entry.config.getPlugin().getModID(), throwable.toString());
                    return null;
                });
    }

    private void initializeWatchService() {
        if (watchService == null) return;
        Path dir = Paths.get("./config");
        try {
            WatchKey watchKey = dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            asyncService.scheduleAtFixedRate(() -> {
                List<WatchEvent<?>> events = watchKey.pollEvents();
                events.forEach(this::processWatchEvent);
            }, 0L, 1000L, TimeUnit.MILLISECONDS);
        } catch (IOException exception) {
            logger.fatal(marker, "Watch service init failed, disabling automatic file synchronization");
        }
    }

    private void processWatchEvent(WatchEvent<?> event) {
        Path path = (Path) event.context();
        String filePath = path.toString();
        if (!ourPaths.contains(filePath))
            return;
        if (expectedChanges.contains(filePath)) {
            expectedChanges.remove(filePath);
            return;
        }
        String name = filePath.replaceAll("\\..+$", "");
        Configuration.getConfig(name).ifPresent(config -> {
            Entry entry = entryList.get(config.getPlugin().getModID());
            runUpdate(entry, UpdateAction.LOAD_WRITE);
        });
    }

    private void runUpdate(Entry entry, UpdateAction action) {
        expectedChanges.add(entry.filePath);
        switch (action) {
            case LOAD_WRITE:
                try {
                    ConfigHandler.loadData(entry.config, new File(CONFIG_DIR, entry.filePath));
                } catch (IOException ioe) {
                    throw new ConfigLoadDataException(entry.config.getPlugin(), ioe);
                }
            case WRITE:
                ConfigHandler.write(entry);
                break;
        }
    }

    static class Entry {

        final ModConfig config;
        final String filePath;

        public Entry(ModConfig config, String filePath) {
            this.config = config;
            this.filePath = filePath;
        }
    }

    public enum UpdateAction {
        LOAD_WRITE,
        WRITE
    }
}
