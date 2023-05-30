package net.frozenorb.foxtrot.commands.op.eotw.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import net.frozenorb.foxtrot.HCF;
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

        HCF.getInstance().getServerHandler().setPreEOTW(!HCF.getInstance().getServerHandler().isPreEOTW());

        HCF.getInstance().getDeathbanMap().wipeDeathbans();

        if (HCF.getInstance().getServerHandler().isPreEOTW()) {
            for (Player player : HCF.getInstance().getServer().getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1F, 1F);
            }

            HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
            HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█" + " " + ChatColor.DARK_RED + "[Pre-EOTW]");
            HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "EOTW is about to commence.");
            HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "████" + ChatColor.RED + "██" + " " + ChatColor.RED + "PvP Protection is disabled.");
            HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED + "All players have been un-deathbanned.");
            HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█" + " " + ChatColor.RED + "All deathbans are now permanent.");
            HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
        } else {
            sender.sendMessage(ChatColor.RED + "The server is no longer in Pre-EOTW mode.");
        }
    }

    @Subcommand("stop")
    public static void eotwStop(Player sender) {
        BukkitTask eotwRunnable = HCF.getInstance().getEotwHandler().eotwRunnable;
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }

        if (eotwRunnable != null){
            eotwRunnable.cancel();
        }


        HCF.getInstance().getServerHandler().setPreEOTW(false);


        sender.sendMessage(CC.translate("&cYou have cancelled the PreEOTW timer."));
        if (eotwRunnable != null){
            eotwRunnable.cancel();
        }
    }
}
