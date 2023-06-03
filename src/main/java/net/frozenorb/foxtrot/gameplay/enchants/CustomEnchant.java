package net.frozenorb.foxtrot.gameplay.enchants;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.enchants.events.PlayerArmorEquipEvent;
import net.frozenorb.foxtrot.gameplay.enchants.events.PlayerArmorUnequipEvent;
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

    @EventHandler
    public void onArmorEquip(PlayerArmorEquipEvent event) {
        refreshEnchants(event.getPlayer());
    }

    @EventHandler
    public void onArmorUnEquip(PlayerArmorUnequipEvent event) {
        refreshEnchants(event.getPlayer());
    }
}
