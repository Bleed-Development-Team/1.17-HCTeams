package net.frozenorb.foxtrot.extras.enchants.listeners;

import net.frozenorb.foxtrot.extras.enchants.events.PlayerArmorEquipEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedListener implements Listener {
    @EventHandler
    public void onArmorEquip(PlayerArmorEquipEvent event) {
        refreshSpeed(event.getPlayer());
    }
    public void refreshSpeed(Player player) {
        ItemStack boot = player.getInventory().getBoots();
        if (boot == null) return;

    }
}
