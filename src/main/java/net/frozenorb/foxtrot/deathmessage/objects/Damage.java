package net.frozenorb.foxtrot.deathmessage.objects;

import lombok.Getter;
import net.frozenorb.foxtrot.HCF;
import org.bukkit.ChatColor;

public abstract class Damage {

    @Getter private String damaged;
    @Getter private double damage;
    @Getter private long time;

    public Damage(String damaged, double damage) {
        this.damaged = damaged;
        this.damage = damage;
        this.time = System.currentTimeMillis();
    }

    public abstract String getDeathMessage();

    public String wrapName(String player) {
        int kills = HCF.getInstance().getMapHandler().getStatsHandler().getStats(player).getKills();

        return (ChatColor.RED + player + ChatColor.DARK_RED + "[" + kills + "]" + ChatColor.WHITE);
    }

    public long getTimeDifference() {
        return System.currentTimeMillis() - time;
    }

}