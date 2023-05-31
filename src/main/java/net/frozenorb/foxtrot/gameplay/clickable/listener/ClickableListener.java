package net.frozenorb.foxtrot.gameplay.clickable.listener;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.clickable.ClickableItem;
import net.frozenorb.foxtrot.gameplay.clickable.ClickableItemHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ClickableListener implements Listener {

    public ClickableItemHandler clickableItemHandler;

    public ClickableListener(ClickableItemHandler clickableItemHandler){
        this.clickableItemHandler = clickableItemHandler;
    }

    @EventHandler
    public void interact(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ClickableItem clickableItem = clickableItemHandler.getClickableItem(player.getItemInHand());

        if (clickableItem == null) return;
        if (clickableItem.isOnCooldown(player)) return;

        clickableItem.handle(event);
    }


}