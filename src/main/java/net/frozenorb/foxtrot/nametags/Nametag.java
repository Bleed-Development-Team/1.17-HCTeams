package net.frozenorb.foxtrot.nametags;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;

public class Nametag {

    public void setNametag(Player player, Player target, ChatColor color){
        Scoreboard scoreboard = getScoreboard(player);
        Team team = getTeam(color, scoreboard);
        team.setPrefix(color.toString());
        if (!team.hasEntry(target.getName())) {
            resetNameTag(player, target);
            team.addEntry(target.getName());
        }
        player.setScoreboard(scoreboard);



    }
    public void resetNameTag(Player player, Player target) {
        if (player != null && target != null && !player.equals(target)) {
            Scoreboard scoreboard = getScoreboard(player);
            Objective objective = scoreboard.getObjective(DisplaySlot.BELOW_NAME);
            if (objective != null)
                objective.unregister();
            for (ChatColor color : ChatColor.values()) {
                Team team = scoreboard.getTeam(getTeamName(color));
                if (team != null)
                    team.removeEntry(target.getName());
            }
        }
    }
    private Scoreboard getScoreboard(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        return scoreboard.equals(Bukkit.getServer().getScoreboardManager().getMainScoreboard()) ?
                Bukkit.getServer().getScoreboardManager().getNewScoreboard() : scoreboard;
    }

    private Team getTeam(ChatColor color, Scoreboard scoreboard) {
        Team team = scoreboard.getTeam(getTeamName(color));
        return (team == null) ? scoreboard.registerNewTeam(getTeamName(color)) : team;
    }
    private String getTeamName(ChatColor color) {
        return "nt_team_" + color.ordinal();
    }
}
