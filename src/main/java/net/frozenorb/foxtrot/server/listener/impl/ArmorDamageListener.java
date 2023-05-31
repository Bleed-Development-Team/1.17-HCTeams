package net.frozenorb.foxtrot.server.listener.impl;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;

import java.util.Random;

public class ArmorDamageListener implements Listener {

    private static final Random random = new Random();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onItemDamage(PlayerItemDamageEvent e) {

        e.setCancelled(true);
    }
}
