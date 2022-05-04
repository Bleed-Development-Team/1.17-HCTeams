package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.PersistMap;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ReclaimMap extends PersistMap<Boolean> {

    public ReclaimMap() {
        super("Reclaimed", "Reclaimed");
    }

    @Override
    public String getRedisValue(Boolean a) {
        return String.valueOf(a);
    }

    @Override
    public Object getMongoValue(Boolean a) {
        return a;
    }

    @Override
    public Boolean getJavaObject(String str) {
        return Boolean.parseBoolean(str);
    }

    public void setReclaimed(UUID uuid, boolean reclaimed) {
        updateValueAsync(uuid, reclaimed);
    }

    public boolean hasReclaimed(UUID uuid){
        return contains(uuid) ? getValue(uuid) : false;
    }
}

