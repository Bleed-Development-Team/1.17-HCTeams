package net.frozenorb.foxtrot.server;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.player.PlayerMoveEvent;

public interface RegionMoveHandler {

    RegionMoveHandler ALWAYS_TRUE = new RegionMoveHandler() {

        @Override
        public boolean handleMove(PlayerMoveEvent event) {
            return (true);
        }

    };

    RegionMoveHandler PVP_TIMER = event -> {
        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot do this while your PVP Timer is active!");
            event.getPlayer().sendMessage(ChatColor.RED + "Type '" + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + "' to remove your timer.");
            event.setTo(event.getFrom());
            return (false);
        }

        return (true);
    };

    public boolean handleMove(PlayerMoveEvent event);

}