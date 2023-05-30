package net.frozenorb.foxtrot.gameplay.enchants.listeners;

import net.frozenorb.foxtrot.gameplay.enchants.events.PlayerArmorEquipEvent;
import net.frozenorb.foxtrot.gameplay.enchants.events.PlayerArmorUnequipEvent;
import net.frozenorb.foxtrot.util.CC;
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
        PotionEffect effect = player.getPotionEffect(PotionEffectType.FIRE_RESISTANCE);
        
        if (effect != null) {
            if (effect.getAmplifier() == 1 || effect.getAmplifier() == 0) {
                player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
            }
        }

        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate == null) return;
        if (chestplate.getItemMeta().getLore() == null) return;

        if (chestplate.getItemMeta().getLore().contains(CC.translate("&4FireResistance I"))) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
        }

    }
    @EventHandler
    public void onArmorDequipd(PlayerArmorUnequipEvent event) {
        refreshFireResistance(event.getPlayer());
    }
}
