package net.frozenorb.foxtrot.events.koth.commands.kothschedule;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;

public class KOTHRegenerateSchedule {

    @Command(value = {"KOTHSchedule Regenerate", "KOTHSchedule Regen"}, async = true)
    @Permission(value = "foxtrot.koth.admin")
    public static void kothScheduleEnable(@Sender CommandSender sender) {
        File kothSchedule = new File(Foxtrot.getInstance().getDataFolder(), "eventSchedule.json");

        if (kothSchedule.delete()) {
            Foxtrot.getInstance().getEventHandler().loadSchedules();

            sender.sendMessage(ChatColor.YELLOW + "The event schedule has been regenerated.");
        } else {
            sender.sendMessage(ChatColor.RED + "Couldn't delete event schedule file.");
        }
    }

    @Command(value = {"KOTHSchedule debug"})
    @Permission(value = "op")
    public static void kothScheduleDebug(@Sender CommandSender sender) {
        Foxtrot.getInstance().getEventHandler().fillSchedule();
        sender.sendMessage(ChatColor.GREEN + "The event schedule has been filled.");
    }
}
