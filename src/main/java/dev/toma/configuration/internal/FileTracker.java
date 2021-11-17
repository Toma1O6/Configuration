package dev.toma.configuration.internal;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.api.IConfigPlugin;
import dev.toma.configuration.api.ModConfig;
import dev.toma.configuration.api.client.IModID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FileTracker {

    public static final FileTracker INSTANCE = new FileTracker();
    private static final File CONFIG_DIR = new File(".", "config");
    private final Logger logger = LogManager.getLogger("Configuration");
    private final Marker marker = MarkerManager.getMarker("FileTracker");
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final List<Entry> entryList = new ArrayList<>();
    private Queue<Update> scheduledUpdates;
    private Future<?> updateTask;

    public synchronized void registerConfigTrackingEntry(ModConfig config) {
        registerConfigTrackingEntry(config, true);
    }

    public synchronized void initialize(Collection<ModConfig> configs) {
        scheduledUpdates = new ArrayDeque<>(configs.size());
        if(!CONFIG_DIR.exists()) {
            CONFIG_DIR.mkdirs();
        }
        if(!CONFIG_DIR.isDirectory()) {
            throw new IllegalStateException("Config file must be a directory!");
        }
        for (ModConfig config : configs) {
            registerConfigTrackingEntry(config, false);
        }
        updateSchedulerJob();
    }

    public synchronized void scheduleConfigUpdate(IModID iModID, UpdateAction action) {
        Entry entry = null;
        String modID = iModID.getModID();
        for (Entry e : entryList) {
            if(e.plugin.getModID().equals(modID)) {
                entry = e;
                break;
            }
        }
        if(entry == null) {
            logger.error("Failed to schedule config update for {}", modID);
            return;
        }
        Update update = new Update(modID, entry, action);
        if(!scheduledUpdates.contains(update)) {
            if(!scheduledUpdates.offer(update)) {
                logger.error("Failed to schedule config update for {}", modID);
            }
        }
    }

    private void registerConfigTrackingEntry(ModConfig config, boolean refreshScheduler) {
        IConfigPlugin plugin = config.getPlugin();
        File configFile = new File(CONFIG_DIR, plugin.getConfigFileName() + ".json");
        if (!configFile.exists()) {
            logger.error(marker, "Couldn't locate config file {}, excluding {} from FileTracker", configFile.getAbsolutePath(), plugin.getModID());
            return;
        }
        entryList.add(new Entry(plugin, configFile.getName(), configFile.lastModified()));
        logger.info(marker, "Added {} plugin into FileTracker", plugin.getModID());
        if (refreshScheduler) {
            updateSchedulerJob();
        }
    }

    private void updateSchedulerJob() {
        if (updateTask != null) {
            updateTask.cancel(false);
        }
        if (!entryList.isEmpty()) {
            updateTask = executorService.scheduleAtFixedRate(() -> checkAndUpdateFiles(CONFIG_DIR), 10L, 4, TimeUnit.SECONDS);
        }
    }

    private void checkAndUpdateFiles(File dir) {
        Iterator<Entry> itr = entryList.iterator();
        while (itr.hasNext()) {
            Entry entry = itr.next();
            File configFile = new File(dir, entry.filePath);
            if(!configFile.exists()) {
                itr.remove();
                continue;
            }
            long lastModified = entry.modified;
            long fileModified = configFile.lastModified();
            if(lastModified != fileModified) {
                scheduleConfigUpdate(entry.plugin, UpdateAction.LOAD_WRITE);
            }
        }
        Update update;
        while ((update = scheduledUpdates.poll()) != null) {
            Optional<ModConfig> optional = Configuration.getConfig(update.modid);
            if (optional.isPresent()) {
                ModConfig config = optional.get();
                IConfigPlugin plugin = config.getPlugin();
                try {
                    switch (update.action) {
                        case LOAD_WRITE:
                            ConfigHandler.loadData(config, new File(dir, update.entry.filePath));
                            ConfigHandler.write(config, update.entry);
                            break;
                        case WRITE:
                            ConfigHandler.write(config, update.entry);
                            break;
                    }
                } catch (Exception e) {
                    logger.error("Error updating config from {} plugin", update.modid);
                }
            }
        }
    }

    static class Entry {

        final IConfigPlugin plugin;
        final String filePath;
        long modified;

        public Entry(IConfigPlugin plugin, String filePath, long modified) {
            this.plugin = plugin;
            this.filePath = filePath;
            this.modified = modified;
        }
    }

    static class Update {

        final String modid;
        final Entry entry;
        final UpdateAction action;

        public Update(String modid, Entry entry, UpdateAction action) {
            this.modid = modid;
            this.entry = entry;
            this.action = action;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Update update = (Update) o;
            return modid.equals(update.modid);
        }

        @Override
        public int hashCode() {
            return modid.hashCode();
        }
    }

    public enum UpdateAction {
        LOAD_WRITE,
        WRITE
    }
}
