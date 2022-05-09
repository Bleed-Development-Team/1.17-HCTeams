package net.frozenorb.foxtrot.server.pearllogs;

import com.google.common.collect.Table;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;


public class PearlLogHandler {

    public static Map<UUID, Location> thrownPearls = new HashMap<>();
    public static Map<UUID, Location> landedPearls = new HashMap<>();


    public static void addPearl(Player player, Location thrown, Location ended) {
        thrownPearls.put(player.getUniqueId(), thrown);
        landedPearls.put(player.getUniqueId(), ended);
    }



}
