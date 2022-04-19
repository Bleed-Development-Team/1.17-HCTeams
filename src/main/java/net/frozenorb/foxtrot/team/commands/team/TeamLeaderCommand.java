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

@SuppressWarnings("deprecation")
public class TeamLeaderCommand {

    @Command(value ={ "team newleader", "t newleader", "f newleader", "faction newleader", "fac newleader", "team leader", "t leader", "f leader", "faction leader", "fac leader" })
    public static void teamLeader(@Sender Player sender, @Name("player") OfflinePlayer player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!team.isOwner(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only the team leader can do this.");
            return;
        }

        if (!team.isMember(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + player.getName() + " is not on your team.");
            return;
        }

        team.sendMessage(ChatColor.DARK_AQUA + player.getName() + " has been given ownership of " + team.getName() + ".");
        team.setOwner(player.getUniqueId());
        team.addCaptain(sender.getUniqueId());
    }

}