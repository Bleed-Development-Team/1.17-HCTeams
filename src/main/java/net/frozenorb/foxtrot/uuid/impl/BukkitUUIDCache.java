package net.frozenorb.foxtrot.uuid.impl;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.uuid.UUIDCache;

import java.util.UUID;

public final class BukkitUUIDCache implements UUIDCache {
    public UUID uuid(String name) {
        return HCF.getInstance().getServer().getOfflinePlayer(name).getUniqueId();
    }

    public String name(UUID uuid) {
        return HCF.getInstance().getServer().getOfflinePlayer(uuid).getName();
    }

    public void ensure(UUID uuid) {}

    public void update(UUID uuid, String name) {}
}
