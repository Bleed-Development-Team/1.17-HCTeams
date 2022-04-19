package net.frozenorb.foxtrot.team.commands.team;

import com.google.common.collect.ImmutableMap;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import net.frozenorb.foxtrot.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

@SuppressWarnings("deprecation")
public class TeamKickCommand {

    @Command(value = {"team kick", "t kick", "f kick", "faction kick", "fac kick"})
    public static void teamKick(@Sender Player sender, @Name("player") OfflinePlayer player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!(team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (!team.isMember(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + player.getName() + " isn't on your team!");
            return;
        }

        if (team.isOwner(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You cannot kick the team leader!");
            return;
        }

        if(team.isCoLeader(player.getUniqueId()) && (!team.isOwner(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.RED + "Only the owner can kick other co-leaders!");
            return;
        }

        if (team.isCaptain(player.getUniqueId()) && !team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only an owner or co-leader can kick other captains!");
            return;
        }

        Player bukkitPlayer = Foxtrot.getInstance().getServer().getPlayer(player.getUniqueId());

        if (bukkitPlayer != null && SpawnTagHandler.isTagged(bukkitPlayer)) {
            sender.sendMessage(ChatColor.RED + bukkitPlayer.getName() + " is currently combat-tagged! You can forcibly kick " + bukkitPlayer.getName() + " by using '"
                    + ChatColor.YELLOW + "/f forcekick " + bukkitPlayer.getName() + ChatColor.RED + "' which will cost your team 1 DTR.");
            return;
        }

        if (team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null && bukkitPlayer != null){
            LunarClientAPI.getInstance().removeWaypoint(bukkitPlayer, new LCWaypoint(
                    team.getFocusedTeam().getName() + "'s HQ",
                    team.getFocusedTeam().getHQ(),
                    Color.BLUE.hashCode(),
                    true
            ));
        }

        if (team.getTeamRally() != null && bukkitPlayer != null){
            LunarClientAPI.getInstance().removeWaypoint(bukkitPlayer, new LCWaypoint(
                    "Team Rally",
                    team.getTeamRally(),
                    Color.AQUA.hashCode(),
                    true
            ));
        }

        if (team.getHQ() != null && bukkitPlayer != null){
            LunarClientAPI.getInstance().removeWaypoint(bukkitPlayer, new LCWaypoint(
                    "HQ",
                    team.getHQ(),
                    Color.BLUE.hashCode(),
                    true
            ));
        }

        team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player.getUniqueId()) + " was kicked by " + sender.getName() + "!");

        TeamActionTracker.logActionAsync(team, TeamActionType.MEMBER_KICKED, ImmutableMap.of(
                "playerId", player,
                "kickedById", sender.getUniqueId(),
                "kickedByName", sender.getName(),
                "usedForceKick", "false"
        ));

        if (team.removeMember(player.getUniqueId())) {
            team.disband();
        } else {
            team.flagForSave();
        }

        Foxtrot.getInstance().getTeamHandler().setTeam(player.getUniqueId(), null);

        if (player.isOnline() && team.getHQ() != null){
            LunarClientAPI.getInstance().removeWaypoint(bukkitPlayer, new LCWaypoint("HQ", team.getHQ(), 0, true));
        }
    }

}