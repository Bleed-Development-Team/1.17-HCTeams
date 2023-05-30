package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.UUID;

public class RaidableTeamsMap extends PersistMap<Integer> {

    public RaidableTeamsMap() {
        super("Raidable Teams", "Raidable Teams");
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

    public int getRaidableTeams(UUID check) {
        return (contains(check) ? getValue(check) : 0);
    }

    public void setRaidableTeams(UUID update, int kills) {
        updateValueAsync(update, kills);
        HCF.getInstance().getKdrMap().updateKDR(update);
    }

    public void add(UUID update, int kills) {
        setRaidableTeams(update, getRaidableTeams(update) + kills);
    }

}
