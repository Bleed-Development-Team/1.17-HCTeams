package net.frozenorb.foxtrot.team.commands;

import me.vaperion.blade.annotation.*;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ForceJoinCommand {

    @Command(value ={ "ForceJoin" })
    @Permission(value = "foxtrot.forcejoin")
    public static void forceJoin(@Sender Player sender, @Name("team") Team team, @Optional("self") Player player) {
        if (Foxtrot.getInstance().getTeamHandler().getTeam(player) != null) {
            if (player == sender) {
                sender.sendMessage(ChatColor.RED + "Leave your current team before attempting to forcejoin.");
            } else {
                sender.sendMessage(ChatColor.RED + "That player needs to leave their current team first!");
            }

            return;
        }

        team.addMember(player.getUniqueId());
        Foxtrot.getInstance().getTeamHandler().setTeam(player.getUniqueId(), team);
        player.sendMessage(ChatColor.YELLOW + "You are now a member of " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + "!");

        if (player != sender) {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.YELLOW + " added to " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + "!");
        }
    }

}