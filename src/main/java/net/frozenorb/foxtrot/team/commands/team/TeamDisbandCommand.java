package net.frozenorb.foxtrot.team.commands.team;

import com.google.common.collect.ImmutableMap;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

public class TeamDisbandCommand {

    @Command(value={ "team disband", "t disband", "f disband", "faction disband", "fac disband" })
    public static void teamDisband(@Sender Player player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team == null){
            player.sendMessage(ChatColor.RED + "You are not on a team!");
            return;
        }

        if (!team.isOwner(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You must be the leader of the team to disband it!");
            return;
        }

        if (team.isRaidable()) {
            player.sendMessage(ChatColor.RED + "You cannot disband your team while raidable.");
            return;
        }

        if (team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null){
            Bukkit.getOnlinePlayers().stream().filter(all -> team.isMember(all.getUniqueId()))
                    .forEach(all -> LunarClientAPI.getInstance().removeWaypoint(all, new LCWaypoint(
                            team.getFocusedTeam().getName() + "'s HQ",
                            team.getFocusedTeam().getHQ(),
                            Color.BLUE.hashCode(),
                            true
                    )));
        }

        if (team.getTeamRally() != null){
            Bukkit.getOnlinePlayers().stream().filter(all -> team.isMember(all.getUniqueId()))
                    .forEach(all -> LunarClientAPI.getInstance().removeWaypoint(player, new LCWaypoint(
                            "Team Rally",
                            team.getTeamRally(),
                            Color.AQUA.hashCode(),
                            true
                    )));
        }

        if (team.getHQ() != null){
            Bukkit.getOnlinePlayers().stream().filter(all -> team.isMember(player.getUniqueId())).forEach(all -> LunarClientAPI.getInstance().removeWaypoint(all, new LCWaypoint(
                    "HQ",
                    team.getHQ(),
                    Color.BLUE.hashCode(),
                    true
            )));
        }

        team.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + player.getName() + " has disbanded the team.");

        TeamActionTracker.logActionAsync(team, TeamActionType.PLAYER_DISBAND_TEAM, ImmutableMap.of(
                "playerId", player.getUniqueId(),
                "playerName", player.getName()
        ));

        team.disband();
    }

}