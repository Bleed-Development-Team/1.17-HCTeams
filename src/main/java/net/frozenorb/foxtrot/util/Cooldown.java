package net.frozenorb.foxtrot.util;

import net.frozenorb.foxtrot.scoreboard.ScoreFunction;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Cooldown {

    public static HashMap<String, HashMap<UUID, Long>> cooldown;

    public static void createCooldown(final String k) {
        if (Cooldown.cooldown.containsKey(k)) {
            throw new IllegalArgumentException("Cooldown already exists.");
        }
        Cooldown.cooldown.put(k, new HashMap<UUID, Long>());
    }

    public static HashMap<UUID, Long> getCooldownMap(final String k) {
        if (Cooldown.cooldown.containsKey(k)) {
            return Cooldown.cooldown.get(k);
        }
        return null;
    }

    public static void addCooldown(final String k, final Player p, final int seconds) {
        if (!Cooldown.cooldown.containsKey(k)) {
            throw new IllegalArgumentException(k + " does not exist");
        }
        final long next = System.currentTimeMillis() + seconds * 1000L;
        Cooldown.cooldown.get(k).put(p.getUniqueId(), next);
    }

    public static boolean isOnCooldown(final String k, final Player p) {
        return Cooldown.cooldown.containsKey(k) && Cooldown.cooldown.get(k).containsKey(p.getUniqueId()) && System.currentTimeMillis() <= Cooldown.cooldown.get(k).get(p.getUniqueId());
    }

    public static boolean isOnCooldown(final String k, final UUID uuid) {
        return Cooldown.cooldown.containsKey(k) && Cooldown.cooldown.get(k).containsKey(uuid) && System.currentTimeMillis() <= Cooldown.cooldown.get(k).get(uuid);
    }

    public static int getCooldownForPlayerInt(final String k, final Player p) {
        return (int)(Cooldown.cooldown.get(k).get(p.getUniqueId()) - System.currentTimeMillis()) / 1000;
    }

    public static long getCooldownForPlayerLong(final String k, final Player p) {
        return Cooldown.cooldown.get(k).get(p.getUniqueId()) - System.currentTimeMillis();
    }

    public static void removeCooldown(final String k, final Player p) {
        if (!Cooldown.cooldown.containsKey(k)) {
            throw new IllegalArgumentException(k + " does not exist");
        }
        Cooldown.cooldown.get(k).remove(p.getUniqueId());
    }

    public static void removeCooldown(final String k, final UUID uuid) {
        if (!Cooldown.cooldown.containsKey(k)) {
            throw new IllegalArgumentException(k + " does not exist");
        }
        Cooldown.cooldown.get(k).remove(uuid);
    }
    public static String getCooldownString(Player player, String cooldown){
        if(!Cooldown.isOnCooldown(cooldown, player)) return "0.0s";
        return ScoreFunction.TIME_FANCY.apply((float) Cooldown.getCooldownForPlayerLong(cooldown, player) / 1000f);
    }

    static {
        Cooldown.cooldown = new HashMap<String, HashMap<UUID, Long>>();
    }


}

