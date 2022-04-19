package net.frozenorb.foxtrot.team.commands.team;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamRenameCommand {

    @Command(value={ "team rename", "t rename", "f rename", "faction rename", "fac rename" })
    public static void teamRename(@Sender Player sender, @Name("new name") String newName) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (Foxtrot.getInstance().getCitadelHandler().getCappers().contains(team.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Citadel cappers cannot change their name. Please contact an admin to rename your team.");
            return;
        }

        if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only team owners and co-leaders can use this command!");
            return;
        }

        if (newName.length() > 16) {
            sender.sendMessage(ChatColor.RED + "Maximum team name size is 16 characters!");
            return;
        }

        if (newName.length() < 3) {
            sender.sendMessage(ChatColor.RED + "Minimum team name size is 3 characters!");
            return;
        }

        if (!TeamCreateCommand.ALPHA_NUMERIC.matcher(newName).find()) {
            if (Foxtrot.getInstance().getTeamHandler().getTeam(newName) == null) {
                team.rename(newName);
                sender.sendMessage(ChatColor.GREEN + "Team renamed to " + newName);
            } else {
                sender.sendMessage(ChatColor.RED + "A team with that name already exists!");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Team names must be alphanumeric!");
        }
    }

}