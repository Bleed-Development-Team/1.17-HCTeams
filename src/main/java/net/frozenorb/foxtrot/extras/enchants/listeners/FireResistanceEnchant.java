package net.frozenorb.foxtrot.extras.enchants.listeners;

import net.frozenorb.foxtrot.extras.enchants.events.PlayerArmorEquipEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FireResistanceEnchant implements Listener {
    @EventHandler
    public void onArmorEquip(PlayerArmorEquipEvent event) {
        refreshFireResistance(event.getPlayer());
    }
    public static void refreshFireResistance(Player player) {
        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate == null) return;
        if (chestplate.getItemMeta().getLore() == null) return;

        if (chestplate.getItemMeta().getLore().contains("Fire Resistance")) {
            if (player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
                player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
            }
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
        }

    }
}
