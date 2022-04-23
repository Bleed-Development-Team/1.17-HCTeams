package net.frozenorb.foxtrot.extras.ability.packages;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class AbilityPackageHandler implements Listener {
    @EventHandler
    public void onAbilityPackage(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!player.getInventory().getItemInMainHand().equals(Foxtrot.getInstance().getAbilityPackage().getPackage()) || player.getInventory().getItemInOffHand().equals(Foxtrot.getInstance().getAbilityPackage().getPackage()))  return;
        ItemStack partnerPackage = null;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item.equals(Foxtrot.getInstance().getAbilityPackage().getPackage())) {
                partnerPackage = item;
                break;
            }
        }
        if (partnerPackage == null) return;
        partnerPackage.setAmount(partnerPackage.getAmount() - 1);

        //if()
    }

}
