package net.frozenorb.foxtrot.extras.enchants.listeners;

import net.frozenorb.foxtrot.extras.enchants.events.PlayerArmorEquipEvent;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.BardClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
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

        if (boot.getItemMeta().getLore().contains("&4Speed II")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        }
    }

    public boolean qualifies(PlayerInventory armor) {
        return wearingAllArmor(armor) &&
                armor.getHelmet().getType() == Material.GOLDEN_HELMET &&
                armor.getChestplate().getType() == Material.GOLDEN_CHESTPLATE &&
                armor.getLeggings().getType() == Material.GOLDEN_LEGGINGS &&
                armor.getBoots().getType() == Material.GOLDEN_BOOTS;
    }

    protected boolean wearingAllArmor(PlayerInventory armor) {
        return (armor.getHelmet() != null &&
                armor.getChestplate() != null &&
                armor.getLeggings() != null &&
                armor.getBoots() != null);
    }
}
