package net.frozenorb.foxtrot.team.commands;

import me.vaperion.blade.annotation.*;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ForceDisbandAllCommand {

    private static Runnable confirmRunnable;

    @Command(value={ "forcedisbandall" })
    @Permission(value = "op")
    public static void forceDisbandAll(@Sender CommandSender sender) {
        confirmRunnable = () -> {
            List<Team> teams = new ArrayList<>();

            for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
                teams.add(team);
            }

            for (Team team : teams) {
                team.disband();
            }

            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + "All teams have been forcibly disbanded!");
        };

        sender.sendMessage(ChatColor.RED + "Are you sure you want to disband all factions? Type " + ChatColor.DARK_RED + "/forcedisbandall confirm" + ChatColor.RED + " to confirm or " + ChatColor.GREEN + "/forcedisbandall cancel" + ChatColor.RED +" to cancel.");
    }

    @Command(value = {"forcedisbandall confirm"})
    @Permission(value = "op")
    public static void confirm(@Sender CommandSender sender) {
        if (confirmRunnable == null) {
            sender.sendMessage(ChatColor.RED + "Nothing to confirm.");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "If you're sure...");
        confirmRunnable.run();
    }

    @Command(value = {"forcedisbandall cancel"})
    @Permission(value = "op")
    public static void cancel(@Sender CommandSender sender) {
        if (confirmRunnable == null) {
            sender.sendMessage(ChatColor.RED + "Nothing to cancel.");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "Cancelled.");
        confirmRunnable = null;
    }

}