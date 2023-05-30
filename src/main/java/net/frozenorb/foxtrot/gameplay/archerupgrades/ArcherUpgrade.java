package net.frozenorb.foxtrot.gameplay.archerupgrades;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public abstract class ArcherUpgrade {

    public abstract String getName();
    public abstract PotionEffect getEffect();
    public abstract DyeColor getDyeColor();
    public abstract String getColor();
    public abstract int getCost();

    public boolean isWearing(Player player){
        if (!isWearingSet(player.getInventory())) return false;

        PlayerInventory armor = player.getInventory();
        LeatherArmorMeta helmet = (LeatherArmorMeta) armor.getHelmet().getItemMeta();
        LeatherArmorMeta chestplate = (LeatherArmorMeta) armor.getChestplate().getItemMeta();
        LeatherArmorMeta leggings = (LeatherArmorMeta) armor.getLeggings().getItemMeta();
        LeatherArmorMeta boots = (LeatherArmorMeta) armor.getBoots().getItemMeta();

        Color helmetColor = helmet.getColor();
        Color chestplateColor = chestplate.getColor();
        Color leggingsColor = leggings.getColor();
        Color bootsColor = boots.getColor();

        if (helmetColor.asRGB() == getDyeColor().getColor().asRGB() &&
                chestplateColor.asRGB() == getDyeColor().getColor().asRGB()
                && leggingsColor.asRGB() == getDyeColor().getColor().asRGB() && bootsColor.asRGB() == getDyeColor().getColor().asRGB()){
            return true;
        }
        return false;
    }

    public boolean isWearingSet(PlayerInventory armor){
        return wearingAllArmor(armor) &&
                armor.getHelmet().getType() == Material.LEATHER_HELMET &&
                armor.getChestplate().getType() == Material.LEATHER_CHESTPLATE &&
                armor.getLeggings().getType() == Material.LEATHER_LEGGINGS &&
                armor.getBoots().getType() == Material.LEATHER_BOOTS;
    }

    public boolean wearingAllArmor(PlayerInventory armor){
        return (armor.getHelmet() != null &&
                armor.getChestplate() != null &&
                armor.getLeggings() != null &&
                armor.getBoots() != null);
    }
}
