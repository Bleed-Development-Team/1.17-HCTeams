package net.frozenorb.foxtrot.extras.ability.util;

import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class Items {

    public static ItemStack getBoneAbility(){
        return ItemBuilder.of(Material.STICK)
                .name("&6&lAnti-Build Stick")
                .addToLore("&7Hit a player 3 times with this ability", "&7to prevent them from placing and breaking blocks.")
                .enchant(Enchantment.DURABILITY, 1)
                .build();
    }

    public static ItemStack getPowerstone(){
        return ItemBuilder.of(Material.PURPLE_DYE)
                .name("&5&lPowerstone")
                .addToLore("&7Right click to receive 5 seconds", "&7of Strength II, Resistance III, and Speed III.")
                .enchant(Enchantment.DURABILITY, 1)
                .build();
    }

    public static ItemStack getComboAbility(){
        return ItemBuilder.of(Material.PUFFERFISH)
                .name("&6&lCombo Ability")
                .addToLore("&7For every hit you land, you", "&7will receive a second of Strength II.")
                .build();
    }

    public static ItemStack getSnowball(){
        return ItemBuilder.of(Material.SNOWBALL)
                .name("&f&lSnowball")
                .addToLore("&7Right click to throw a snowball", "&7that will switch your position", "&7with the player you hit.")
                .build();
    }

    public static ItemStack getPotionCounter(){
        return ItemBuilder.of(Material.STICK)
                .name("&3&lPotion Counter")
                .addToLore("&7Checks how many potions a player has.")
                .build();
    }

    public static ItemStack getMedkit(){
        return ItemBuilder.of(Material.PAPER)
                .name("&6&lMed Kit")
                .addToLore("&7Right click to receive Resistance III & Regeneration III", "&7for 4 seconds.")
                .amount(1)
                .build();
    }

    public static ItemStack getAntiPearl(){
        return ItemBuilder.of(Material.ENDER_EYE)
                .name("&3&lAnti-Pearl")
                .addToLore("&7Puts a player on ender pearl cooldown.")
                .enchant(Enchantment.DURABILITY, 1)
                .amount(1)
                .build();
    }

    public static ItemStack getRocket(){
        return ItemBuilder.of(Material.FIREWORK_ROCKET)
                .name("&c&lRocket")
                .addToLore("&7Right click to launch yourself up into the air.")
                .build();
    }

    public static ItemStack getBackToTheRoots(){
        return ItemBuilder.of(Material.BONE_MEAL)
                .name("&f&lBack to the Roots")
                .addToLore("&7Hit a player 3 times to remove hit cooldown for 5 seconds")
                .build();
    }

}
