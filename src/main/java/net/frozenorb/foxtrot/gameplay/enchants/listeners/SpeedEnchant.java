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

public class SpeedEnchant implements Listener {

    public static void refreshSpeed(Player player) {
        ItemStack boot = player.getInventory().getBoots();
        PotionEffect effect = player.getPotionEffect(PotionEffectType.SPEED);

        if (effect != null) {
            if (effect.getAmplifier() == 1 || effect.getAmplifier() == 0) {
                player.removePotionEffect(PotionEffectType.SPEED);
            }
        }

        if (boot == null) return;
        if (boot.getItemMeta().getLore() == null) return;
        if (boot.getItemMeta().getLore().contains(CC.translate("&4Speed II")))
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));

    }
}
