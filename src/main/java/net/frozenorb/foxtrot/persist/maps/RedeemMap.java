package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.UUID;

public class RedeemMap extends PersistMap<Boolean> {

    public RedeemMap() {
        super("Redeemed", "Redeemed");
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

    public void setRedeemed(UUID uuid, boolean redeemed) {
        updateValueAsync(uuid, redeemed);
    }

    public boolean hasRedeemed(UUID uuid){
        return contains(uuid) ? getValue(uuid) : false;
    }
}

