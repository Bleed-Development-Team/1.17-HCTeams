package net.frozenorb.foxtrot.events.koth.commands.kothschedule;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class KOTHDisableSchedule {

    @Command(value = "KOTHSchedule Disable")
    @Permission(value = "foxtrot.koth.admin")
    public static void kothScheduleDisable(@Sender CommandSender sender) {
        Foxtrot.getInstance().getEventHandler().setScheduleEnabled(false);

        sender.sendMessage(ChatColor.YELLOW + "The KOTH schedule has been " + ChatColor.RED + "disabled" + ChatColor.YELLOW + ".");
    }

}
