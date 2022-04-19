package net.frozenorb.foxtrot.team.commands.team;

import com.google.common.collect.ImmutableMap;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamUnShadowMuteCommand {

    @Command(value={ "team unshadowmute", "t unshadowmute", "f unshadowmute", "faction unshadowmute", "fac unshadowmute" })
    @Permission(value = "foxtrot.mutefaction")
    public static void teamUnShadowMute(@Sender Player sender, @Name("team") Team team) {
        TeamActionTracker.logActionAsync(team, TeamActionType.TEAM_MUTE_EXPIRED, ImmutableMap.of(
                "shadowMute", "true"
        ));

        TeamShadowMuteCommand.getTeamShadowMutes().entrySet().removeIf(mute -> mute.getValue().equalsIgnoreCase(team.getName()));

        sender.sendMessage(ChatColor.GRAY + "Un-shadowmuted the team " + team.getName() + ChatColor.GRAY  + ".");
    }

}