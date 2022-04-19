package net.frozenorb.foxtrot.team.commands;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.TeamHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PowerFactionCommand {

    @Command(value ={ "powerfaction add", "team powerfaction add", "pf add", "powerfac add" })
    @Permission(value = "foxtrot.powerfactions")
    public static void powerFactionAdd(@Sender Player sender, @Name("team") Team team) {
        team.setPowerFaction(true);
        sender.sendMessage(ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + " is now a power faction!");
    }

    @Command(value ={ "powerfaction remove", "team powerfaction remove", "pf remove", "powerfac remove" })
    @Permission(value = "foxtrot.powerfactions")
    public static void powerFactionRemove(@Sender Player sender, @Name("team") Team team) {
        team.setPowerFaction(false);
        sender.sendMessage(ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + " is no longer a power faction!");
    }

    @Command(value ={ "powerfaction list", "team powerfaction list", "pf list", "powerfac list" })
    @Permission(value = "foxtrot.powerfactions")
    public static void powerFactionList(@Sender Player sender) {
        sender.sendMessage(ChatColor.YELLOW + "Found " + ChatColor.RED + TeamHandler.getPowerFactions().size() + ChatColor.YELLOW + " Power Factions.");
        int i = 1;
        for( Team t : TeamHandler.getPowerFactions() ) {
            sender.sendMessage(ChatColor.YELLOW + "" + i + ". " + ChatColor.RED + t.getName());
            i++;
        }
    }
}