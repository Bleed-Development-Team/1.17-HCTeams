package net.frozenorb.foxtrot.team.commands.team;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class TeamSetHQCommand {

    @Command(value={ "team sethq", "t sethq", "f sethq", "faction sethq", "fac sethq", "team sethome", "t sethome", "f sethome", "faction sethome", "fac sethome", "sethome", "sethq" })
    public static void teamSetHQ(@Sender Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId())) {
            if (LandBoard.getInstance().getTeam(sender.getLocation()) != team) {
                if (!sender.isOp()) {
                    sender.sendMessage(ChatColor.RED + "You can only set HQ in your team's territory.");
                    return;
                } else {
                    sender.sendMessage(ChatColor.RED.toString() + ChatColor.ITALIC + "Setting HQ outside of your team's territory would normally be disallowed, but this check is being bypassed due to your rank.");
                }
            }

            /*
             * Removed at Jon's request.
             * https://github.com/FrozenOrb/HCTeams/issues/59
             */

//            if (sender.getLocation().getBlockY() > 100) {
//                if (!sender.isOp()) {
//                    sender.sendMessage(ChatColor.RED + "You can't set your HQ above  Y 100.");
//                    return;
//                } else {
//                    sender.sendMessage(ChatColor.RED.toString() + ChatColor.ITALIC + "Claiming above Y 100 would normally be disallowed, but this check is being bypassed due to your rank.");
//                }
//            }

            if (team.getHQ() != null){
                Bukkit.getOnlinePlayers().stream().filter(player -> team.isMember(player.getUniqueId())).forEach(player -> LunarClientAPI.getInstance().removeWaypoint(player, new LCWaypoint("HQ", team.getHQ(), Color.BLUE.hashCode(), true, true)));

            }

            team.setHQ(sender.getLocation());
            team.sendMessage(ChatColor.DARK_AQUA + sender.getName() + " has updated the team's HQ point!");

            Bukkit.getOnlinePlayers().stream().filter(player -> team.isMember(player.getUniqueId())).forEach(player -> LunarClientAPI.getInstance().sendWaypoint(player, new LCWaypoint("HQ", team.getHQ(), Color.BLUE.hashCode(), true, true)));

        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
    }

}