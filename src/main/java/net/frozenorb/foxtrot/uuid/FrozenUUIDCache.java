package net.frozenorb.foxtrot.uuid;

import com.google.common.base.Preconditions;
import net.frozenorb.foxtrot.Foxtrot;

import java.util.UUID;

public final class FrozenUUIDCache {
    private static UUIDCache impl = null;

    public static UUIDCache getImpl() {
        return impl;
    }

    private static boolean initiated = false;

    public static void init() {
        Preconditions.checkState(!initiated);
        initiated = true;
        try {
            impl = (UUIDCache)Class.forName(Foxtrot.getInstance().getConfig().getString("UUIDCache.Backend", "net.frozenorb.foxtrot.uuid.impl.RedisUUIDCache")).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new UUIDListener(), Foxtrot.getInstance());
    }

    public static UUID uuid(String name) {
        return impl.uuid(name);
    }

    public static String name(UUID uuid) {
        return impl.name(uuid);
    }

    public static void ensure(UUID uuid) {
        impl.ensure(uuid);
    }

    public static void update(UUID uuid, String name) {
        impl.update(uuid, name);
    }
}
