package net.frozenorb.foxtrot.team.commands.team;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class TeamLeaveCommand {

    @Command(value={ "team leave", "t leave", "f leave", "faction leave", "fac leave" })
    public static void teamLeave(@Sender Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isOwner(sender.getUniqueId()) && team.getSize() > 1) {
            sender.sendMessage(ChatColor.RED + "Please choose a new leader before leaving your team!");
            return;
        }

        if (LandBoard.getInstance().getTeam(sender.getLocation()) == team) {
            sender.sendMessage(ChatColor.RED + "You cannot leave your team while on team territory.");
            return;
        }

        if(SpawnTagHandler.isTagged(sender)) {
            sender.sendMessage(ChatColor.RED + "You are combat-tagged! You can only leave your faction by using '" + ChatColor.YELLOW + "/f forceleave" + ChatColor.RED + "' which will cost your team 1 DTR.");
            return;
        }

        if (team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null){
            LunarClientAPI.getInstance().removeWaypoint(sender, new LCWaypoint(
                            team.getFocusedTeam().getName() + "'s HQ",
                            team.getFocusedTeam().getHQ(),
                            Color.BLUE.hashCode(),
                            true
                    ));
        }

        if (team.getTeamRally() != null){
            LunarClientAPI.getInstance().removeWaypoint(sender, new LCWaypoint(
                            "Team Rally",
                            team.getTeamRally(),
                            Color.AQUA.hashCode(),
                            true
                    ));
        }

        if (team.getHQ() != null){
           LunarClientAPI.getInstance().removeWaypoint(sender, new LCWaypoint(
                "HQ",
                team.getHQ(),
                Color.BLUE.hashCode(),
                true
            ));
        }

        if (team.removeMember(sender.getUniqueId())) {
            team.disband();
            Foxtrot.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), null);
            sender.sendMessage(ChatColor.DARK_AQUA + "Successfully left and disbanded team!");
        } else {
            Foxtrot.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), null);
            team.flagForSave();
            team.sendMessage(ChatColor.YELLOW + sender.getName() + " has left the team.");

            sender.sendMessage(ChatColor.DARK_AQUA + "Successfully left the team!");
        }

        /*
        FrozenNametagHandler.reloadPlayer(sender);
        FrozenNametagHandler.reloadOthersFor(sender);
         */
    }

}