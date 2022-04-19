package net.frozenorb.foxtrot.team.commands.team;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamPromoteCommand {

    @Command(value={ "team promote", "t promote", "f promote", "faction promote", "fac promote", "team captain" })
    public static void teamPromote(@Sender Player sender, @Name("player") OfflinePlayer player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender.getUniqueId());

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team co-leaders (and above) can do this.");
            return;
        }

        if (!team.isMember(player.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + player.getName() + " is not on your team.");
            return;
        }

        if (team.isOwner(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + player.getName() + " is already a leader.");
        } else if (team.isCoLeader(player.getUniqueId())) {
            if (team.isOwner(sender.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + player.getName() + " is already a co-leader! To make them a leader, use /t leader");
            } else {
                sender.sendMessage(ChatColor.RED + "Only the team leader can promote new leaders.");
            }
        } else if (team.isCaptain(player.getUniqueId())) {
            if (team.isOwner(sender.getUniqueId())) {
                team.sendMessage(ChatColor.DARK_AQUA + player.getName() + " has been promoted to Co-Leader!");
                team.addCoLeader(player.getUniqueId());
                team.removeCaptain(player.getUniqueId());
            } else {
                sender.sendMessage(ChatColor.RED + "Only the team leader can promote new Co-Leaders.");
            }
        } else {
            team.sendMessage(ChatColor.DARK_AQUA + player.getName() + " has been promoted to Captain!");
            team.addCaptain(player.getUniqueId());
        }
    }

}