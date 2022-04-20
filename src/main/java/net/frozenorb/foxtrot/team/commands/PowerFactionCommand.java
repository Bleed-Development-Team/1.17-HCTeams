package net.frozenorb.foxtrot.team.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.TeamHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("powerfaction|pf")
@CommandPermission("foxtrot.team.powerfaction")
public class PowerFactionCommand extends BaseCommand {


    @Subcommand("add")
    @Description("Add a team to the power faction list")
    public static void powerFactionAdd(Player sender, Team team) {
        team.setPowerFaction(true);
        sender.sendMessage(ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + " is now a power faction!");
    }


    @Subcommand("remove")
    @Description("Remove a team from the power faction list")
    public static void powerFactionRemove(Player sender, Team team) {
        team.setPowerFaction(false);
        sender.sendMessage(ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + " is no longer a power faction!");
    }


    @Subcommand("list")
    @Description("List all power factions")
    public static void powerFactionList(Player sender) {
        sender.sendMessage(ChatColor.YELLOW + "Found " + ChatColor.RED + TeamHandler.getPowerFactions().size() + ChatColor.YELLOW + " Power Factions.");
        int i = 1;
        for( Team t : TeamHandler.getPowerFactions() ) {
            sender.sendMessage(ChatColor.YELLOW + "" + i + ". " + ChatColor.RED + t.getName());
            i++;
        }
    }
}