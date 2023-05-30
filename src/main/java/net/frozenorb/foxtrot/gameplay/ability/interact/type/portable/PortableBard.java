package net.frozenorb.foxtrot.gameplay.ability.interact.type.portable;

import net.frozenorb.foxtrot.gameplay.ability.interact.InteractAbility;
import net.frozenorb.foxtrot.gameplay.ability.interact.type.portable.menu.PortableBardMenu;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class PortableBard extends InteractAbility {
    @Override
    public String getID() {
        return "portablebard";
    }

    @Override
    public String getName() {
        return "&e&lPortable Bard";
    }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public ItemStack getItemStack() {
        return ItemBuilder.of(Material.ORANGE_DYE)
                .name(getName())
                .addToLore("&7Right click to give yourself", "&7specific effects.")
                .enchant(Enchantment.DURABILITY, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    @Override
    public String getColor() {
        return "&e";
    }

    @Override
    public void handle(PlayerInteractEvent event, Player player) {
        new PortableBardMenu(player).updateMenu();
    }
}
