package net.frozenorb.foxtrot.team.commands.team;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.UUIDUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamCaptainCommand {

    @Command(value ={ "team captain add", "t captain add", "t mod add", "team mod add", "f mod add", "fac mod add", "faction mod add", "f captain add", "fac captain add", "faction captain add"})
    public static void captainAdd(@Sender Player sender, @Name("player") OfflinePlayer promote) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender.getUniqueId());
        if( team == null ) {
            sender.sendMessage(ChatColor.RED + "You must be in a team to execute this command.");
            return;
        }

        if(!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only team co-leaders can execute this command.");
            return;
        }

        if(!team.isMember(promote.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "This player must be a member of your team.");
            return;
        }

        if(team.isOwner(promote.getUniqueId()) || team.isCaptain(promote.getUniqueId()) || team.isCoLeader(promote.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "This player is already a captain (or above) of your team.");
            return;
        }

        team.removeCoLeader(promote.getUniqueId());
        team.addCaptain(promote.getUniqueId());
        team.sendMessage(org.bukkit.ChatColor.DARK_AQUA + UUIDUtils.name(promote.getUniqueId()) + " has been promoted to Captain!");
    }

    @Command(value={ "team captain remove", "t captain remove", "t mod remove", "team mod remove", "f mod remove", "fac mod remove", "faction mod remove", "f captain remove", "fac captain remove", "faction captain remove" })
    public static void captainRemove(@Sender Player sender, @Name("player") OfflinePlayer demote) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender.getUniqueId());
        if( team == null ) {
            sender.sendMessage(ChatColor.RED + "You must be in a team to execute this command.");
            return;
        }

        if(!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only team co-leaders can execute this command.");
            return;
        }

        if(!team.isMember(demote.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "This player must be a member of your team.");
            return;
        }

        if(!team.isCaptain(demote.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "This player is not a captain of your team.");
            return;
        }

        team.removeCoLeader(demote.getUniqueId());
        team.removeCaptain(demote.getUniqueId());
        team.sendMessage(org.bukkit.ChatColor.DARK_AQUA + UUIDUtils.name(demote.getUniqueId()) + " has been demoted to a member!");
    }

}
