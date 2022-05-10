package net.frozenorb.foxtrot.extras.enchants.listeners;

import net.frozenorb.foxtrot.extras.enchants.events.PlayerArmorEquipEvent;
<<<<<<< Updated upstream
=======
import net.frozenorb.foxtrot.extras.enchants.events.PlayerArmorUnequipEvent;
import org.bukkit.Bukkit;
>>>>>>> Stashed changes
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedEnchant implements Listener {
    @EventHandler
    public void onArmorEquip(PlayerArmorEquipEvent event) {
        refreshSpeed(event.getPlayer());
    }

    @EventHandler
    public void unEquip(PlayerArmorEquipEvent event) {
        refreshSpeed(event.getPlayer());
    }

    @EventHandler
    public void join(PlayerJoinEvent event){
        refreshSpeed(event.getPlayer());
    }

    public static void refreshSpeed(Player player) {
        PotionEffect effect = player.getPotionEffect(PotionEffectType.SPEED);
        if (effect != null) {
            if (effect.getAmplifier() == 1 || effect.getAmplifier() == 0) {
                player.removePotionEffect(PotionEffectType.SPEED);
            }
        }
        ItemStack boot = player.getInventory().getBoots();
        if (boot == null) return;
        if (boot.getItemMeta().getLore() == null) return;

<<<<<<< Updated upstream
        if (boot.getItemMeta().getLore().contains("&4Speed II")) {
            if (player.hasPotionEffect(PotionEffectType.SPEED)) {
                player.removePotionEffect(PotionEffectType.SPEED);
            }
=======
        if (boot.getItemMeta().getLore().contains("Speed II")) {
>>>>>>> Stashed changes
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        }
    }
}
