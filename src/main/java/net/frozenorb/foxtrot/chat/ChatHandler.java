package net.frozenorb.foxtrot.chat;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import lombok.Getter;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.chat.listeners.ChatListener;
import net.frozenorb.foxtrot.chat.tasks.SaveCustomPrefixesTask;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatHandler {

    private static File customPrefixesFile;
    @Getter private static AtomicInteger publicMessagesSent = new AtomicInteger();

    private Map<UUID, String> customPrefixes = new HashMap<>();

    public ChatHandler() {
        customPrefixesFile = new File(HCF.getInstance().getDataFolder(), "customPrefixes.json");
        HCF.getInstance().getServer().getPluginManager().registerEvents(new ChatListener(), HCF.getInstance());
        (new SaveCustomPrefixesTask()).runTaskTimerAsynchronously(HCF.getInstance(), 5 * 60 * 20, 5 * 60 * 20); // Every 5 minutes.
        reloadCustomPrefixes();
    }

    public void reloadCustomPrefixes() {
        long started = System.currentTimeMillis();

        try {
            if (!customPrefixesFile.exists()) {
                customPrefixesFile.createNewFile();
            }

            BasicDBObject json = (BasicDBObject) JSON.parse(FileUtils.readFileToString(customPrefixesFile));
            customPrefixes.clear();

            for (Map.Entry<String, Object> prefixEntry : ((BasicDBObject) json.get("prefixes")).entrySet()) {
                customPrefixes.put(UUID.fromString(prefixEntry.getKey()), ChatColor.translateAlternateColorCodes('&', prefixEntry.getValue().toString()));
            }

            int loaded = customPrefixes.size();
            long timeElapsed = System.currentTimeMillis() - started;
            HCF.getInstance().getLogger().warning("Loaded " + loaded + " custom chat prefix" + (loaded == 1 ? "" : "es") + " in " + timeElapsed + "ms");
        } catch (Exception e) {
            HCF.getInstance().getLogger().warning("Failed to load custom chat prefixes: " + e.getMessage());
        }
    }

    public void saveCustomPrefixes() {
        try {
            long started = System.currentTimeMillis();

            BasicDBObject json = new BasicDBObject("prefixes", customPrefixes);
            FileUtils.write(customPrefixesFile, HCF.GSON.toJson(new JsonParser().parse(json.toString())));

            int loaded = customPrefixes.size();
            long timeElapsed = System.currentTimeMillis() - started;
            HCF.getInstance().getLogger().warning("Saved " + loaded + " custom chat prefix" + (loaded == 1 ? "" : "es") + " in " + timeElapsed + "ms");
        } catch (Exception e) {
            HCF.getInstance().getLogger().warning("Failed to save custom chat prefixes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean hasCustomPrefix(UUID player) {
        return customPrefixes.containsKey(player);
    }

    public String getCustomPrefix(UUID player) {
        return customPrefixes.getOrDefault(player, "");
    }

    public void setCustomPrefix(UUID player, String customPrefix) {
        if (customPrefix == null || customPrefix.isEmpty()) {
            customPrefixes.remove(player);
        } else {
            customPrefixes.put(player, customPrefix);
        }
    }

    public Collection<Map.Entry<UUID, String>> getAllCustomPrefixes() {
        return ImmutableMap.copyOf(customPrefixes).entrySet();
    }

}