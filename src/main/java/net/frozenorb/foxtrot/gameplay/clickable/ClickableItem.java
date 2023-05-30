package net.frozenorb.foxtrot.gameplay.clickable;

import net.frozenorb.foxtrot.util.Cooldown;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public abstract class ClickableItem {

    public ClickableItem(){
        if (Objects.equals(getCooldownID(), "")) return;
        Cooldown.createCooldown(getCooldownID());
    }

    public abstract String getID();
    public abstract ItemStack getItemStack();
    public abstract int getCooldown();
    public abstract String getCooldownID();
    public abstract void handle(PlayerInteractEvent event);

    public boolean isOnCooldown(Player player){
        if (getCooldown() == 0) return false;
        if (Cooldown.isOnCooldown(getCooldownID(), player)) return true;

        return false;
    }

    public void takeItem(Player player){
        ItemStack itemStack = player.getItemInHand();

        if (itemStack.getAmount() == 1){
            player.setItemInHand(null);
        } else {
            itemStack.setAmount(itemStack.getAmount() - 1);
        }
    }
}
