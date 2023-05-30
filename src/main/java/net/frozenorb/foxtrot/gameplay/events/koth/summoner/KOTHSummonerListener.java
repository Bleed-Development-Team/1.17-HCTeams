package net.frozenorb.foxtrot.gameplay.events.koth.summoner;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class KOTHSummonerListener implements Listener {

    @EventHandler
    public void interact(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (player.getItemInHand().getType() == Material.LEVER && player.getItemInHand().getItemMeta().getDisplayName().equals(CC.translate("&b&lKOTH Summoner"))){
            for (Event otherKoth : HCF.getInstance().getEventHandler().getEvents()) {
                if (otherKoth.isActive()) {
                    player.sendMessage(ChatColor.RED + otherKoth.getName() + " is currently active. You cannot do this now.");
                    return;
                }
            }

            try {
                Event[] eventArray = HCF.getInstance().getEventHandler().getEvents().toArray(new Event[0]);
                Event koth = eventArray[HCF.RANDOM.nextInt(eventArray.length)];

                koth.activate();
            } catch (Exception ex){
                ex.printStackTrace();
                player.sendMessage(CC.translate("&cAn error has occurred, please report this to an administrator."));
                return;
            }

            if (player.getItemInHand().getAmount() == 1){
                player.getItemInHand().setType(Material.AIR);
            } else {
                player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
            }
        }
    }
}
