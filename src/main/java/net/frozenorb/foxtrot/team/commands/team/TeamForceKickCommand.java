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
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import net.frozenorb.foxtrot.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

@SuppressWarnings("deprecation")
public class TeamForceKickCommand {

    @Command(value = {"team forcekick", "t forcekick", "f forcekick", "faction forcekick", "fac forcekick"})
    public static void teamForceKick(@Sender Player sender, @Name("player") OfflinePlayer player) {
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
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player.getUniqueId()) + " isn't on your team!");
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

        if (team.isCaptain(player.getUniqueId()) && (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.RED + "Only an owner or co-leader can kick other captains!");
            return;
        }

        Player bukkitPlayer = Foxtrot.getInstance().getServer().getPlayer(player.getUniqueId());


        if (team.getFocusedTeam().getHQ() != null && bukkitPlayer != null){
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

        TeamActionTracker.logActionAsync(team, TeamActionType.MEMBER_KICKED, ImmutableMap.of(
                "playerId", player,
                "kickedById", sender.getUniqueId(),
                "kickedByName", sender.getName(),
                "usedForceKick", "true"
        ));

        if (team.removeMember(player.getUniqueId())) {
            team.disband();
        } else {
            team.flagForSave();
        }

        Foxtrot.getInstance().getTeamHandler().setTeam(player.getUniqueId(), null);

        if (SpawnTagHandler.isTagged(bukkitPlayer)) {
            team.setDTR(team.getDTR() - 1);
            team.sendMessage(ChatColor.RED + UUIDUtils.name(player.getUniqueId()) + " was force kicked by " + sender.getName() + " and your team lost 1 DTR!");
            long dtrCooldown;
            if (team.isRaidable()) {
                TeamActionTracker.logActionAsync(team, TeamActionType.TEAM_NOW_RAIDABLE, ImmutableMap.of());
                dtrCooldown = System.currentTimeMillis() + Foxtrot.getInstance().getMapHandler().getRegenTimeRaidable();
            } else {
                dtrCooldown = System.currentTimeMillis() + Foxtrot.getInstance().getMapHandler().getRegenTimeDeath();
            }

            team.setDTRCooldown(dtrCooldown);
            DTRHandler.markOnDTRCooldown(team);
        } else {
            team.sendMessage(ChatColor.RED + UUIDUtils.name(player.getUniqueId()) + " was force kicked by " + sender.getName() + "!");
        }

        if (bukkitPlayer != null) {
            //FrozenNametagHandler.reloadPlayer(bukkitPlayer);
            //FrozenNametagHandler.reloadOthersFor(bukkitPlayer);
        }
    }

}