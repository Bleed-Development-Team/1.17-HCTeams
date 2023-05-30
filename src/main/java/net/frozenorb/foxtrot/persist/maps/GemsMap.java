package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.UUID;

public class GemsMap extends PersistMap<Integer> {

    public GemsMap() {
        super("Gems", "Gems");
    }

    @Override
    public String getRedisValue(Integer kills) {
        return (String.valueOf(kills));
    }

    @Override
    public Integer getJavaObject(String str) {
        return (Integer.parseInt(str));
    }

    @Override
    public Object getMongoValue(Integer kills) {
        return (kills);
    }

    public int getGems(UUID check) {
        return (contains(check) ? getValue(check) : 0);
    }

    public void setGems(UUID update, int gems) {
        updateValueAsync(update, gems);
    }

    public void addGems(UUID update, int gems){
        updateValueAsync(update, getGems(update) + gems);
    }
    public void removeGems(UUID update, int gems){
        updateValueAsync(update, getGems(update) - gems);
    }
}