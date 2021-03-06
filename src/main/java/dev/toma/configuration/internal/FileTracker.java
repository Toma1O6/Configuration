package dev.toma.configuration.internal;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.api.IConfigPlugin;
import dev.toma.configuration.api.ModConfig;
import dev.toma.configuration.api.client.IModID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FileTracker {

    public static final FileTracker INSTANCE = new FileTracker();
    private final Logger logger = LogManager.getLogger("FileChecker");
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final List<Entry> entryList = new ArrayList<>();
    private Queue<Update> scheduledUpdates;

    public synchronized void initialize(Collection<ModConfig> configs) {
        scheduledUpdates = new ArrayDeque<>(configs.size());
        File dir = new File(".", "config");
        if(!dir.exists()) {
            dir.mkdirs();
        }
        if(!dir.isDirectory()) {
            throw new IllegalStateException("Config file must be a directory!");
        }
        for (ModConfig config : configs) {
            IConfigPlugin plugin = config.getPlugin();
            File configFile = new File(dir, plugin.getConfigFileName() + ".json");
            if(!configFile.exists()) {
                logger.error("Couldn't locate config file {}, excluding {} from FileChecker", configFile.getAbsolutePath(), plugin.getModID());
                continue;
            }
            entryList.add(new Entry(plugin, configFile.getName(), configFile.lastModified()));
            logger.info("Added {} plugin into FileTracker", plugin.getModID());
        }
        if(!entryList.isEmpty()) {
            executorService.scheduleAtFixedRate(() -> checkAndUpdateFiles(dir), 10L, 4, TimeUnit.SECONDS);
        }
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
