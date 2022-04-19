package net.frozenorb.foxtrot.uuid.impl;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.uuid.UUIDCache;

import java.util.UUID;

public final class BukkitUUIDCache implements UUIDCache {
    public UUID uuid(String name) {
        return Foxtrot.getInstance().getServer().getOfflinePlayer(name).getUniqueId();
    }

    public String name(UUID uuid) {
        return Foxtrot.getInstance().getServer().getOfflinePlayer(uuid).getName();
    }

    public void ensure(UUID uuid) {}

    public void update(UUID uuid, String name) {}
}
