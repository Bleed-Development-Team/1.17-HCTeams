package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.eotw.commands.EOTWCommand;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

@CommandAlias("PreEOTW")
@CommandPermission("foxtrot.eotw")
public class PreEOTWCommand extends BaseCommand {

    @Subcommand("forcestart")
    public static void preeotw(Player sender) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }

        Foxtrot.getInstance().getServerHandler().setPreEOTW(!Foxtrot.getInstance().getServerHandler().isPreEOTW());

        Foxtrot.getInstance().getDeathbanMap().wipeDeathbans();

        if (Foxtrot.getInstance().getServerHandler().isPreEOTW()) {
            for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1F, 1F);
            }

            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█" + " " + ChatColor.DARK_RED + "[Pre-EOTW]");
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "EOTW is about to commence.");
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "████" + ChatColor.RED + "██" + " " + ChatColor.RED + "PvP Protection is disabled.");
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED + "All players have been un-deathbanned.");
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█" + " " + ChatColor.RED + "All deathbans are now permanent.");
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
        } else {
            sender.sendMessage(ChatColor.RED + "The server is no longer in Pre-EOTW mode.");
        }
    }

    @Subcommand("stop")
    public static void eotwStop(Player sender) {
        BukkitTask eotwRunnable = EOTWCommand.preEotwRunnable;
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }

        if (eotwRunnable != null){
            eotwRunnable.cancel();
        }


        Foxtrot.getInstance().getServerHandler().setPreEOTW(false);


        sender.sendMessage(CC.translate("&cYou have cancelled the PreEOTW timer."));
        if (eotwRunnable != null){
            eotwRunnable.cancel();
        }
    }
}
