package net.frozenorb.foxtrot.gameplay.clickable.type;

import net.frozenorb.foxtrot.gameplay.clickable.ClickableItem;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class RankPouch extends ClickableItem {

    @Override
    public String getID() {
        return rank.toLowerCase();
    }

    public ItemStack itemStack;
    public String rank;

    public RankPouch(ItemStack itemStack, String rank){
        this.itemStack = itemStack;
        this.rank = rank;
    }

    @Override
    public ItemStack getItemStack() {
        return itemStack;
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
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + event.getPlayer().getName() + " group add " + rank);
        event.getPlayer().sendMessage(CC.translate("&aSuccesfully redeemed the &r" + rank + " &arank."));

        takeItem(event.getPlayer());
    }
}
