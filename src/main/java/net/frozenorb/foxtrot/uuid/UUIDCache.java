package net.frozenorb.foxtrot.uuid;


import java.util.UUID;

public interface UUIDCache {
    UUID uuid(String paramString);

    String name(UUID paramUUID);

    void ensure(UUID paramUUID);

    void update(UUID paramUUID, String paramString);
}
