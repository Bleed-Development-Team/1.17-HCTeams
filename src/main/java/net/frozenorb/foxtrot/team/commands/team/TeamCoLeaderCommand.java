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

public class TeamCoLeaderCommand {

    @Command(value={ "team coleader add", "t coleader add", "t co-leader add", "team co-leader add", "f co-leader add", "fac co-leader add", "faction co-leader add", "f coleader add", "fac coleader add", "faction coleader add"})
    public static void coleaderAdd(@Sender Player sender, @Name("player") OfflinePlayer promote) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender.getUniqueId());
        if( team == null ) {
            sender.sendMessage(ChatColor.RED + "You must be in a team to execute this command.");
            return;
        }

        if(!team.isOwner(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only the team owner can execute this command.");
            return;
        }

        if(!team.isMember(promote.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "This player must be a member of your team.");
            return;
        }

        if(team.isOwner(promote.getUniqueId()) || team.isCoLeader(promote.getUniqueId())) {
           sender.sendMessage(ChatColor.RED + "This player is already a co-leader (or above) of your team.");
            return;
        }

        team.addCoLeader(promote.getUniqueId());
        team.removeCaptain(promote.getUniqueId());
        team.sendMessage(org.bukkit.ChatColor.DARK_AQUA + UUIDUtils.name(promote.getUniqueId()) + " has been promoted to Co-Leader!");
    }

    @Command(value={ "team coleader remove", "t coleader remove", "t co-leader remove", "team co-leader remove", "f co-leader remove", "fac co-leader remove", "faction co-leader remove", "f coleader remove", "fac coleader remove", "faction coleader remove" })
    public static void coleaderRemove(@Sender Player sender, @Name("player") OfflinePlayer demote) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender.getUniqueId());
        if( team == null ) {
            sender.sendMessage(ChatColor.RED + "You must be in a team to execute this command.");
            return;
        }

        if(!team.isOwner(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only the team owner can execute this command.");
            return;
        }

        if(!team.isMember(demote.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "This player must be a member of your team.");
            return;
        }

        if(!team.isCoLeader(demote.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "This player is not a co-leader of your team.");
            return;
        }

        team.removeCoLeader(demote.getUniqueId());
        team.removeCaptain(demote.getUniqueId());
        team.sendMessage(org.bukkit.ChatColor.DARK_AQUA + UUIDUtils.name(demote.getUniqueId()) + " has been demoted to a member!");
    }
}
