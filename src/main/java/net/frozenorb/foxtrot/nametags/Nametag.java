package net.frozenorb.foxtrot.nametags;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Nametag {

    public void refreshTag(Player player){
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team sbTeam = scoreboard.getTeam(player.getName());

        if (sbTeam == null) {

        }
    }
}
