package net.frozenorb.foxtrot.provider.nametags.listener;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.provider.nametags.Nametag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class NametagListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        HCF.getInstance().getNametagManager().getNametags().remove(player.getUniqueId());
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        HCF.getInstance().getNametagManager().getNametags().put(player.getUniqueId(), new Nametag(player));
        HCF.getInstance().getNametagManager().update();
    }

}
