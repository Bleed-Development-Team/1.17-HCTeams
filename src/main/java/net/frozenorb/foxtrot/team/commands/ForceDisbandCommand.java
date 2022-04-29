package net.frozenorb.foxtrot.team.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("forcedisband")
@CommandPermission("foxtrot.team.forcedisband")
public class ForceDisbandCommand extends BaseCommand {

    @Default
    @Description("Force disbands a team!")
    public static void forceDisband(Player sender, @Name("team") Team team) {
        team.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + sender.getName() + " has force disbanded the team.");
        team.disband();
        sender.sendMessage(ChatColor.YELLOW + "Force disbanded the team " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + ".");
    }

}