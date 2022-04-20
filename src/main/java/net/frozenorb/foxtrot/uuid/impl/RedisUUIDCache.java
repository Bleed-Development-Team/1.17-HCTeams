package net.frozenorb.foxtrot.uuid.impl;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.uuid.UUIDCache;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class RedisUUIDCache implements UUIDCache {
    private static final Map<UUID, String> uuidToName = new ConcurrentHashMap<>();

    private static final Map<String, UUID> nameToUuid = new ConcurrentHashMap<>();

    public RedisUUIDCache() {
        Foxtrot.getInstance().runBackboneRedisCommand(redis -> {
            Map<String, String> cache = redis.hgetAll("UUIDCache");
            for (Map.Entry<String, String> cacheEntry : cache.entrySet()) {
                UUID uuid = UUID.fromString(cacheEntry.getKey());
                String name = cacheEntry.getValue();
                uuidToName.put(uuid, name);
                nameToUuid.put(name.toLowerCase(), uuid);
            }
            return null;
        });
    }

    public UUID uuid(String name) {
        return nameToUuid.get(name.toLowerCase());
    }

    public String name(UUID uuid) {
        return uuidToName.get(uuid);
    }

    public void ensure(UUID uuid) {
        if (String.valueOf(name(uuid)).equals("null"))
            Foxtrot.getInstance().getLogger().warning(uuid + " didn't have a cached name.");
    }

    public void update(final UUID uuid, final String name) {
        uuidToName.put(uuid, name);
        for (Map.Entry<String, UUID> entry : (new HashMap<>(nameToUuid)).entrySet()) {
            if (!entry.getValue().equals(uuid))
                continue;
            nameToUuid.remove(entry.getKey());
        }
        nameToUuid.put(name.toLowerCase(), uuid);
        (new BukkitRunnable() {
            public void run() {
                Foxtrot.getInstance().runBackboneRedisCommand(redis -> {
                    redis.hset("UUIDCache", uuid.toString(), name);
                    return null;
                });
            }
        }).runTaskAsynchronously(Foxtrot.getInstance());
    }
}
