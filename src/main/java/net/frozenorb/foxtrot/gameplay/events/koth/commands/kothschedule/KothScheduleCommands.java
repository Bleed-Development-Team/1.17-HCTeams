package net.frozenorb.foxtrot.gameplay.events.koth.commands.kothschedule;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

@CommandAlias("KOTHSchedule")
@CommandPermission("op")
public class KothScheduleCommands extends BaseCommand {
    @Subcommand("disable")
    public static void kothScheduleDisable(CommandSender sender) {
        HCF.getInstance().getEventHandler().setScheduleEnabled(false);

        sender.sendMessage(ChatColor.YELLOW + "The KOTH schedule has been " + ChatColor.RED + "disabled" + ChatColor.YELLOW + ".");
    }
    @Subcommand("enable")
    public static void kothScheduleEnable(CommandSender sender) {
        HCF.getInstance().getEventHandler().setScheduleEnabled(true);

        sender.sendMessage(ChatColor.YELLOW + "The KOTH schedule has been " + ChatColor.GREEN + "enabled" + ChatColor.YELLOW + ".");
    }
    @Subcommand("reload")
    public static void kothScheduleReload(Player sender) {
        HCF.getInstance().getEventHandler().loadSchedules();
        sender.sendMessage(CC.translate("&3&l[KOTH] ") + ChatColor.YELLOW + "Reloaded the KOTH schedule.");
    }
    @Subcommand("debug")
    public static void kothScheduleDebug(CommandSender sender) {
        HCF.getInstance().getEventHandler().fillSchedule();
        sender.sendMessage(ChatColor.GREEN + "The event schedule has been filled.");
    }
    @Subcommand("regen|regenerate")
    public static void kothScheduleRegen(CommandSender sender) {
        File kothSchedule = new File(HCF.getInstance().getDataFolder(), "eventSchedule.json");

        if (kothSchedule.delete()) {
            HCF.getInstance().getEventHandler().loadSchedules();

            sender.sendMessage(ChatColor.YELLOW + "The event schedule has been regenerated.");
        } else {
            sender.sendMessage(ChatColor.RED + "Couldn't delete event schedule file.");
        }
    }

}
