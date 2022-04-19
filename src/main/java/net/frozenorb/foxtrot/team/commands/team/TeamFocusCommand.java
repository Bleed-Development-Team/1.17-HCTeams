package net.frozenorb.foxtrot.team.commands.team;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;

public class TeamFocusCommand {

    @Command(value = {"f focus", "faction focus", "team focus", "t focus"})
    public static void focus(@Sender Player player, @Name(value = "team") Team team){
        Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId());

        if (playerTeam == null){
            player.sendMessage(CC.translate("&7You are not a team!"));
            return;
        }

        if (playerTeam.getUniqueId() == team.getUniqueId()){
            player.sendMessage(CC.translate("&cYou can't focus your own team!"));
            return;
        }

        //Removing the current waypoint.
        if (playerTeam.getFocusedTeam() != null && playerTeam.getFocusedTeam().getHQ() != null){
            Bukkit.getOnlinePlayers().stream().filter(all -> playerTeam.isMember(all.getUniqueId()))
                    .forEach(all -> LunarClientAPI.getInstance().removeWaypoint(all, new LCWaypoint(
                            playerTeam.getFocusedTeam().getName() + "'s HQ",
                            playerTeam.getFocusedTeam().getHQ(),
                            Color.BLUE.hashCode(),
                            true
                    )));
        }

        playerTeam.setFocusedTeam(team);
        playerTeam.sendMessage("&d" + team.getName() + " &efaction has been focused by &d" + player.getName() + "&e.");

        // Adding the new focused team's waypoint.

        if (playerTeam.getFocusedTeam().getHQ() != null){
            Bukkit.getOnlinePlayers().stream().filter(all -> playerTeam.isMember(all.getUniqueId()))
                    .forEach(all -> LunarClientAPI.getInstance().sendWaypoint(all, new LCWaypoint(
                            playerTeam.getFocusedTeam().getName() + "'s HQ",
                            playerTeam.getFocusedTeam().getHQ(),
                            Color.BLUE.hashCode(),
                            true
                    )));
        }
    }

    @Command(value = {"f unfocus", "faction unfocus", "team unfocus", "t unfocus"})
    public static void unfocus(@Sender Player player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId());

        if (team == null){
            player.sendMessage(CC.translate("&7You are not on a team!"));
            return;
        }

        if (team.getFocusedTeam() == null){
            player.sendMessage(CC.translate("&cYou aren't currently focusing anyone!"));
            return;
        }

        if (team.getFocusedTeam().getHQ() != null){
            Bukkit.getOnlinePlayers().stream().filter(all -> team.isMember(all.getUniqueId()))
                    .forEach(all -> LunarClientAPI.getInstance().removeWaypoint(all, new LCWaypoint(
                            team.getFocusedTeam().getName() + "'s HQ",
                            team.getFocusedTeam().getHQ(),
                            Color.BLUE.hashCode(),
                            true
                    )));
        }


        team.sendMessage("&d" + team.getFocusedTeam().getName() + " &efaction has been unfocused by &d" + player.getName() + "&e.");
        team.setFocusedTeam(null);
    }
}
