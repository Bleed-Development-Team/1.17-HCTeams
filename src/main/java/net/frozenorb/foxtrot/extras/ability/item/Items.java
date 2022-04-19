package net.frozenorb.foxtrot.extras.ability.item;

import com.google.common.collect.Lists;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;

public class Items {

    public static ItemStack getBone(){
        return ItemBuilder.of(Material.BONE).name("&6&lExotic Bone").setLore(Arrays.asList("&7Hit a player 3 times to restrict", "&7from interacting with any block.")).build();
    }

    public static ItemStack getLauncher(){
        return ItemBuilder.of(Material.FIREWORK_ROCKET).name("&3&lALebab's Launcher").setLore(Collections.singletonList("&7Right Click to be sent forward and upwards.")).build();
    }

    public static ItemStack getNinja(){
        return ItemBuilder.of(Material.NETHER_STAR).name("&3&lNinja Star")
                .enchant(Enchantment.DURABILITY, 1).setLore(Arrays.asList("&7Right click to teleport to", "&7the last person that hit you.")).build();
    }

}
