package net.frozenorb.foxtrot.nametags;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class NametagListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());
        Foxtrot foxtrot = Foxtrot.getInstance();
        foxtrot.getNametagHandler().setNametag(event.getPlayer(), event.getPlayer(), ChatColor.GREEN);



        if (ArcherClass.isMarked(event.getPlayer())) {
            Bukkit.getOnlinePlayers().stream().filter(all -> all != event.getPlayer()).forEach(all -> {
                foxtrot.getNametagHandler().setNametag(all, event.getPlayer(), ChatColor.YELLOW);
            });
            return;
        }
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (all == event.getPlayer()) {
                continue;
            }
            if (foxtrot.getTeamHandler().getTeam(all) == team) {
                foxtrot.getNametagHandler().setNametag(all, event.getPlayer(), ChatColor.GREEN);
                continue;
            }
            foxtrot.getNametagHandler().setNametag(all, event.getPlayer(), ChatColor.RED);
        }
    }
}
