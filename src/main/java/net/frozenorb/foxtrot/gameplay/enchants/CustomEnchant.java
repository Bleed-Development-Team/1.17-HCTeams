package net.frozenorb.foxtrot.gameplay.enchants;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.enchants.listeners.FireResistanceEnchant;
import net.frozenorb.foxtrot.gameplay.enchants.listeners.InvisibilityEnchant;
import net.frozenorb.foxtrot.gameplay.enchants.listeners.SpeedEnchant;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class CustomEnchant implements Listener {

    public static void init() {
        HCF.getInstance().getServer().getPluginManager().registerEvents(new FireResistanceEnchant(), HCF.getInstance());
        HCF.getInstance().getServer().getPluginManager().registerEvents(new SpeedEnchant(), HCF.getInstance());
        HCF.getInstance().getServer().getPluginManager().registerEvents(new InvisibilityEnchant(), HCF.getInstance());

        Bukkit.getServer().getPluginManager().registerEvents(new CustomEnchant(), HCF.getInstance());
    }

    private static void refreshEnchants(Player player) {
        FireResistanceEnchant.refreshFireResistance(player);
        SpeedEnchant.refreshSpeed(player);
        InvisibilityEnchant.refreshInvisibility(player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        refreshEnchants(event.getPlayer());
    }
}
