package net.frozenorb.foxtrot.team.commands.team;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import net.frozenorb.foxtrot.team.event.FullTeamBypassEvent;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

public class TeamAcceptCommand {

    @Command(value = {"team accept", "t accept", "f accept", "faction accept", "fac accept", "team a", "t a", "f a", "faction a", "fac a", "team join", "t join", "f join", "faction join", "fac join", "team j", "t j", "f j", "faction j", "fac j"})
    public static void teamAccept(@Sender Player sender, @Name("team") Team team) {
        if (team.getInvitations().contains(sender.getUniqueId())) {
            if (Foxtrot.getInstance().getTeamHandler().getTeam(sender.getUniqueId()) != null) {
                sender.sendMessage(ChatColor.RED + "You are already on a team!");
                return;
            }

            if (team.getMembers().size() >= Foxtrot.getInstance().getMapHandler().getTeamSize()) {
                FullTeamBypassEvent attemptEvent = new FullTeamBypassEvent(sender, team);
                Foxtrot.getInstance().getServer().getPluginManager().callEvent(attemptEvent);

                if (!attemptEvent.isAllowBypass()) {
                    sender.sendMessage(ChatColor.RED + team.getName() + " cannot be joined: Team is full!");
                    return;
                }
            }

            if (DTRHandler.isOnCooldown(team) && !Foxtrot.getInstance().getServerHandler().isPreEOTW() && !Foxtrot.getInstance().getMapHandler().isKitMap() && !Foxtrot.getInstance().getServerHandler().isVeltKitMap()) {
                sender.sendMessage(ChatColor.RED + team.getName() + " cannot be joined: Team not regenerating DTR!");
                return;
            }

            if (team.getMembers().size() >= 15 && Foxtrot.getInstance().getTeamHandler().isRostersLocked()) {
                sender.sendMessage(ChatColor.RED + team.getName() + " cannot be joined: Team rosters are locked server-wide!");
                return;
            }

            if (SpawnTagHandler.isTagged(sender)) {
                sender.sendMessage(ChatColor.RED + team.getName() + " cannot be joined: You are combat tagged!");
                return;
            }

            if (team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null){
                LunarClientAPI.getInstance().sendWaypoint(sender, new LCWaypoint(
                        team.getFocusedTeam().getName() + "'s HQ",
                        team.getFocusedTeam().getHQ(),
                        Color.BLUE.hashCode(),
                        true
                ));
            }

            if (team.getTeamRally() != null){
                LunarClientAPI.getInstance().sendWaypoint(sender, new LCWaypoint(
                        "Team Rally",
                        team.getTeamRally(),
                        Color.AQUA.hashCode(),
                        true
                ));
            }

            if (team.getHQ() != null){
                LunarClientAPI.getInstance().sendWaypoint(sender, new LCWaypoint(
                        "HQ",
                        team.getHQ(),
                        Color.BLUE.hashCode(),
                        true
                ));
            }


            team.getInvitations().remove(sender.getUniqueId());
            team.addMember(sender.getUniqueId());
            Foxtrot.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), team);

            team.sendMessage(ChatColor.YELLOW + sender.getName() + " has joined the team!");

            //FrozenNametagHandler.reloadPlayer(sender);
            //FrozenNametagHandler.reloadOthersFor(sender);
        } else {
            sender.sendMessage(ChatColor.RED + "This team has not invited you!");
        }
    }

}