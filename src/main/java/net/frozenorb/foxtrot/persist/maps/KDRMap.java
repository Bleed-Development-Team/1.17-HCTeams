package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.UUID;

public class KDRMap extends PersistMap<Double> {

    public KDRMap() {
        super("KDR", "KDR");
    }

    @Override
    public String getRedisValue(Double kdr) {
        return (String.valueOf(kdr));
    }

    @Override
    public Double getJavaObject(String str) {
        return (Double.parseDouble(str));
    }

    @Override
    public Object getMongoValue(Double kdr) {
        return (kdr);
    }

    public void setKDR(UUID update, double kdr) {
        updateValueAsync(update, kdr);
    }

    public void updateKDR(UUID update) {
        setKDR(update, Math.max(((double) HCF.getInstance().getKillsMap().getKills(update)) / Math.max(HCF.getInstance().getDeathsMap().getDeaths(update), 1), 0));
    }
}
