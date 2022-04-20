package net.frozenorb.foxtrot.extras.resoucepack;

import com.comphenix.protocol.PacketType;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public class ResourcePack implements Listener {
    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setResourcePack(Foxtrot.getInstance().getConfig().getString("resource-pack"));
    }

    @EventHandler
    public void onResourcePackInteraction(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();
        if (event.getStatus().equals(PlayerResourcePackStatusEvent.Status.ACCEPTED)) {
            return;
        }
        player.kickPlayer(CC.translate("&e&lFoxtrot &7>> &cResource pack rejected."));
    }
}
