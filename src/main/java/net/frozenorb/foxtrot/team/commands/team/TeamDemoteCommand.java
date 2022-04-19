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

public class TeamDemoteCommand {

    @Command(value ={ "team demote", "t demote", "f demote", "faction demote", "fac demote" })
    public static void teamDemote(@Sender Player sender, @Name("player") OfflinePlayer player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team co-leaders (and above) can do this.");
            return;
        }

        if (!team.isMember(player.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player.getUniqueId()) + " is not on your team.");
            return;
        }

        if (team.isOwner(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player.getUniqueId()) + " is the leader. To change leaders, the team leader must use /t leader <name>");
        } else if (team.isCoLeader(player.getUniqueId())) {
            if (team.isOwner(sender.getUniqueId())) {
                team.removeCoLeader(player.getUniqueId());
                team.addCaptain(player.getUniqueId());
                team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player.getUniqueId()) + " has been demoted to Captain!");
            } else {
                sender.sendMessage(ChatColor.RED + "Only the team leader can demote Co-Leaders.");
            }
        } else if (team.isCaptain(player.getUniqueId())) {
            team.removeCaptain(player.getUniqueId());
            team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player.getUniqueId()) + " has been demoted to a Member!");
        } else {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player.getUniqueId()) + " is currently a member. To kick them, use /t kick");
        }
    }

}