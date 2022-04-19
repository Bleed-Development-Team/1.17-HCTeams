package net.frozenorb.foxtrot.team.commands.team;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;

public class TeamRallyCommand {

    @Command(value = {"f rally", "faction rally", "team rally", "t rally"})
    public static void rally(@Sender Player player){
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId());

        if (team == null){
            player.sendMessage(CC.translate("&7You are not on a team!"));
            return;
        }

        if (team.getTeamRally() != null){
            Bukkit.getOnlinePlayers().stream().filter(all -> team.isMember(all.getUniqueId()))
                    .forEach(all -> LunarClientAPI.getInstance().removeWaypoint(all, new LCWaypoint(
                            "Team Rally",
                            team.getTeamRally(),
                            Color.ORANGE.hashCode(),
                            true
                    )));
        }

        team.setTeamRally(player.getLocation());
        team.sendMessage("&3" + player.getName() + " has updated the team's rally point!");

        Bukkit.getOnlinePlayers().stream().filter(all -> team.isMember(all.getUniqueId()))
                .forEach(all -> LunarClientAPI.getInstance().sendWaypoint(all,
                        new LCWaypoint(
                                "Team Rally",
                                player.getLocation(),
                                Color.ORANGE.hashCode(),
                                true
                        )));
    }

    @Command(value = {"f unrally", "faction unrally", "team unrally", "t unrally"})
    public static void unrally(@Sender Player player){
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId());

        if (team == null){
            player.sendMessage(CC.translate("&7You are not on a team!"));
            return;
        }

        if (team.getTeamRally() == null){
            player.sendMessage(CC.translate("&cYou don't have an active rally!"));
            return;
        }

        team.setTeamRally(null);
        team.sendMessage("&3" + player.getName() + " has removed the team's rally point!");

        Bukkit.getOnlinePlayers().stream().filter(all -> team.isMember(all.getUniqueId()))
                .forEach(all -> LunarClientAPI.getInstance().removeWaypoint(player, new LCWaypoint(
                        "Team Rally",
                        team.getTeamRally(),
                        Color.AQUA.hashCode(),
                        true
                )));
    }
}
