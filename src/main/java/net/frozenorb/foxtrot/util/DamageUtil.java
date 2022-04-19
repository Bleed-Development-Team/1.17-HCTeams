package net.frozenorb.foxtrot.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DamageUtil {


    public static Double calculateDamage(Player player, Entity entity) {
        ItemStack item = player.getItemInHand();
        double damage = 0.00;

        if (item != null && getWeaponDamage(item) != 0.00) {
            damage += getWeaponDamage(item);
            if (item.containsEnchantment(Enchantment.DAMAGE_ALL)) {
                damage += (0.5 * item.getEnchantmentLevel(Enchantment.DAMAGE_ALL));
            }
        } else {
            damage = 0.5;
        }
        return damage;
    }

    public static double getWeaponDamage(ItemStack item) {
        switch (item.getType()) {
            case DIAMOND_SWORD -> {
                return 7.0;
            }
            case IRON_SWORD -> {
                return 6.0;
            }
            case WOODEN_SWORD, GOLDEN_SWORD -> {
                return 4.0;
            }
            case STONE_SWORD -> {
                return 5.0;
            }
        }
        return 0.00;
    }





}
