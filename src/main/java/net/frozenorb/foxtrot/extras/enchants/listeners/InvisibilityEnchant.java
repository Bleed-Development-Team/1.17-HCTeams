package net.frozenorb.foxtrot.extras.enchants.listeners;

import net.frozenorb.foxtrot.extras.enchants.events.PlayerArmorEquipEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class InvisibilityEnchant implements Listener {

    @EventHandler
    public void onArmorEquip(PlayerArmorEquipEvent event) {
        refreshInvisibility(event.getPlayer());
    }

    @EventHandler
    public void unEquip(PlayerArmorEquipEvent event) {
        refreshInvisibility(event.getPlayer());
    }

    @EventHandler
    public void join(PlayerJoinEvent event){
        refreshInvisibility(event.getPlayer());
    }

    public static void refreshInvisibility(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        if (helmet == null) return;
        if (helmet.getItemMeta().getLore() == null) return;

        if (helmet.getItemMeta().getLore().contains("&9Invisibility")) {
            if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
            }
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
        }
    }
}
