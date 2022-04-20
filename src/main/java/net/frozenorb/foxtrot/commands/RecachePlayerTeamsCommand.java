package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

@CommandAlias("playerteamcache")
@CommandPermission("op")
public class RecachePlayerTeamsCommand extends BaseCommand {


    @Subcommand("rebuild")
    public static void recachePlayerTeamsRebuild(Player sender) {
        sender.sendMessage(ChatColor.DARK_PURPLE + "Rebuilding player team cache...");
        Foxtrot.getInstance().getTeamHandler().recachePlayerTeams();
        sender.sendMessage(ChatColor.DARK_PURPLE + "The player death cache has been rebuilt.");
    }


    @Subcommand("check")
    public static void recachePlayerTeams(Player sender) {
        sender.sendMessage(ChatColor.DARK_PURPLE + "Checking player team cache...");
        Map<UUID, String> dealtWith = new HashMap<>();
        Set<UUID> errors = new HashSet<>();

        for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
            for (UUID member : team.getMembers()) {
                if (dealtWith.containsKey(member) && !errors.contains(member)) {
                    errors.add(member);
                    sender.sendMessage(ChatColor.RED + " - " + member + " (Team: " + team.getName() + ", Expected: " + dealtWith.get(member) + ")");
                    continue;
                }

                dealtWith.put(member, team.getName());
            }
        }

        if (errors.size() == 0) {
            sender.sendMessage(ChatColor.DARK_PURPLE + "No errors found while checking player team cache.");
        } else {
            sender.sendMessage(ChatColor.DARK_PURPLE.toString() + errors.size() + " error(s) found while checking player team cache.");
        }
    }

}