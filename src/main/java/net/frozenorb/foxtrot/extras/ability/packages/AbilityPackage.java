package net.frozenorb.foxtrot.extras.ability.packages;

import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AbilityPackage {

    public ItemStack getPackage() {
        return ItemBuilder.of(Material.ENDER_CHEST).name(CC.translate("&c&lAbility Package")).addToLore(CC.translate("&7Right click to receive 3x random partner items from /abilities")).build();
    }
}
