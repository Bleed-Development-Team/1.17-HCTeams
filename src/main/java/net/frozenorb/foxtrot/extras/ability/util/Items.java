package net.frozenorb.foxtrot.extras.ability.util;

import net.frozenorb.foxtrot.util.ItemBuilder;
import net.minecraft.world.item.ItemBanner;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class Items {

    public static ItemStack getBoneAbility(){
        return ItemBuilder.of(Material.STICK)
                .name("&6&lAnti-Build Stick")
                .addToLore("&7Hit a player 3 times with this ability", "&7to prevent them from placing and breaking blocks.")
                .enchant(Enchantment.DURABILITY, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    public static ItemStack getPowerstone(){
        return ItemBuilder.of(Material.PURPLE_DYE)
                .name("&5&lPowerstone")
                .addToLore("&7Right click to receive 5 seconds", "&7of Strength II, Resistance III, and Speed III.")
                .enchant(Enchantment.DURABILITY, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
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
                .enchant(Enchantment.DURABILITY, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    public static ItemStack getAntiPearl(){
        return ItemBuilder.of(Material.ENDER_EYE)
                .name("&3&lAnti-Pearl")
                .addToLore("&7Puts a player on ender pearl cooldown.")
                .enchant(Enchantment.DURABILITY, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
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

    public static ItemStack getLuckyMode(){
        return ItemBuilder.of(Material.GOLD_INGOT)
                .name("&e&lLucky Mode")
                .addToLore("&7Right click to have a chance of ", "&7either getting Strength II or Weakness II.")
                .enchant(Enchantment.DURABILITY, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    public static ItemStack getStrength(){
        return ItemBuilder.of(Material.BLAZE_POWDER)
                .name("&c&lStrength II")
                .addToLore("&7&l* &aTeam Members")
                .enchant(Enchantment.DURABILITY, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    public static ItemStack getResistance(){
        return ItemBuilder.of(Material.IRON_INGOT)
                .name("&e&lResistance III")
                .addToLore("&7&l* &aTeam Members")
                .enchant(Enchantment.DURABILITY, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    public static ItemStack getInvis(){
        return ItemBuilder.of(Material.INK_SAC)
                .name("&b&lInvisibility")
                .addToLore("&7&l* &aTeam Members")
                .enchant(Enchantment.DURABILITY, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    public static ItemStack getRegen(){
        return ItemBuilder.of(Material.GHAST_TEAR)
                .name("&d&lRegeneration III")
                .addToLore("&7&l* &aTeam Members")
                .enchant(Enchantment.DURABILITY, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    public static ItemStack getJump(){
        return ItemBuilder.of(Material.FEATHER)
                .name("&a&lJump VII")
                .addToLore("&7&l* &aTeam Members")
                .build();
    }

    public static ItemStack getPortableBard(){
        return ItemBuilder.of(Material.ORANGE_DYE)
                .name("&6&lPortable Bard")
                .addToLore("&7&l* &aTeam Members")
                .enchant(Enchantment.DURABILITY, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    public static ItemStack getTimeWarp(){
        return ItemBuilder.of(Material.FEATHER)
                .name("&e&lTime-Warp")
                .addToLore("&7Right click to warp yourself", "&7to your last thrown pearl.")
                .enchant(Enchantment.DURABILITY, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    public static ItemStack getNinjaStar() {
        return ItemBuilder.of(Material.NETHER_STAR)
                .name("&b&lNinja Star")
                .addToLore("&7Teleports you to the last person who hit you (within 15 seconds)")
                .build();
    }

    public static ItemStack getTPBow(){
        return ItemBuilder.of(Material.BOW)
                .name("&6&lTeleportation Bow")
                .addToLore("&7Hit someone with this arrow", "&7 to teleport to their location.")
                .enchant(Enchantment.ARROW_INFINITE, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    public static ItemStack getGuardian(){
        return ItemBuilder.of(Material.CLOCK)
                .name("&6&lGuardian Angel")
                .addToLore("&7Right click and if you", "&7go under 3 hearts, you'll be healed.")
                .build();
    }
}
