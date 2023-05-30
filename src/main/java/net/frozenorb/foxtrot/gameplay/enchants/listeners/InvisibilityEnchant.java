package net.frozenorb.foxtrot.gameplay.enchants.listeners;

import net.frozenorb.foxtrot.gameplay.enchants.events.PlayerArmorEquipEvent;
import net.frozenorb.foxtrot.gameplay.enchants.events.PlayerArmorUnequipEvent;
import net.frozenorb.foxtrot.util.CC;
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
        PotionEffect effect = player.getPotionEffect(PotionEffectType.INVISIBILITY);

        if (effect != null) {
            if (effect.getAmplifier() == 1 || effect.getAmplifier() == 0) {
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
            }
        }

        ItemStack helmet = player.getInventory().getHelmet();
        if (helmet == null) return;
        if (helmet.getItemMeta().getLore() == null) return;

        if (helmet.getItemMeta().getLore().contains(CC.translate("&9Invisibility I"))) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
        }
    }

    @EventHandler
    public void onArmorDequipd(PlayerArmorUnequipEvent event) {
        refreshInvisibility(event.getPlayer());
    }
}
