package net.frozenorb.foxtrot.team.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
@CommandAlias("forcedisbandall")
@CommandPermission("op")
public class ForceDisbandAllCommand extends BaseCommand {

    private static Runnable confirmRunnable;


    @Default
    @Description("Force disband all teams.")
    public static void forceDisbandAll(CommandSender sender) {
        confirmRunnable = () -> {

            List<Team> teams = new ArrayList<>(HCF.getInstance().getTeamHandler().getTeams());

            for (Team team : teams) {
                if (team.hasDTRBitmask(DTRBitmask.KOTH) || team.hasDTRBitmask(DTRBitmask.CITADEL) | team.hasDTRBitmask(DTRBitmask.SAFE_ZONE) ||
                team.hasDTRBitmask(DTRBitmask.ROAD) || team.hasDTRBitmask(DTRBitmask.CONQUEST)) continue;

                team.disband();
            }

            HCF.getInstance().getServer().broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + "All teams have been forcibly disbanded!");
        };

        sender.sendMessage(ChatColor.RED + "Are you sure you want to disband all factions? Type " + ChatColor.DARK_RED + "/forcedisbandall confirm" + ChatColor.RED + " to confirm or " + ChatColor.GREEN + "/forcedisbandall cancel" + ChatColor.RED +" to cancel.");
    }


    @Subcommand("confirm")
    @Description("Confirm disband all teams.")
    public static void confirm(CommandSender sender) {
        if (confirmRunnable == null) {
            sender.sendMessage(ChatColor.RED + "Nothing to confirm.");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "If you're sure...");
        confirmRunnable.run();
    }


    @Subcommand("cancel")
    @Description("Cancel disband all teams.")
    public static void cancel(CommandSender sender) {
        if (confirmRunnable == null) {
            sender.sendMessage(ChatColor.RED + "Nothing to cancel.");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "Cancelled.");
        confirmRunnable = null;
    }

}