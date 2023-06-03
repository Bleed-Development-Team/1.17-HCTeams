package net.frozenorb.foxtrot.provider.scoreboard.listener;

import net.frozenorb.foxtrot.HCF;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ScoreboardListener implements Listener {

    @EventHandler
    public void join(PlayerJoinEvent event){
        HCF.getInstance().getScoreboardHandler().create(event.getPlayer());
    }

    @EventHandler
    public void join(PlayerQuitEvent event){
        HCF.getInstance().getScoreboardHandler().delete(event.getPlayer());
    }
}
