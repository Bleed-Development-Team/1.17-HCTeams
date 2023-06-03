package net.frozenorb.foxtrot.gameplay.clickable.type;

import net.frozenorb.foxtrot.gameplay.clickable.ClickableItem;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class MoneyPack extends ClickableItem {
    @Override
    public String getID() {
        return "moneypack";
    }

    @Override
    public ItemStack getItemStack() {
        return ItemBuilder.of(Material.BOOK)
                .name("&a&lMoney &fPack")
                .addToLore("", "&a| &fRight click to receive", "&a| &f$1000 added to your balance.", "")
                .build();
    }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public String getCooldownID() {
        return "";
    }

    @Override
    public void handle(PlayerInteractEvent event) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "addbal " + event.getPlayer().getName() + " 1000");

        takeItem(event.getPlayer());
    }
}
