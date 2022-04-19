package net.frozenorb.foxtrot.events.koth.commands.kothschedule;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class KOTHEnableSchedule {

    @Command(value = "KOTHSchedule Enable")
    @Permission(value = "foxtrot.koth.admin")
    public static void kothScheduleEnable(@Sender CommandSender sender) {
        Foxtrot.getInstance().getEventHandler().setScheduleEnabled(true);

        sender.sendMessage(ChatColor.YELLOW + "The KOTH schedule has been " + ChatColor.GREEN + "enabled" + ChatColor.YELLOW + ".");
    }

}
