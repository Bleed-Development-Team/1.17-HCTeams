package net.frozenorb.foxtrot.events.koth.commands.kothschedule;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHReloadSchedule {

    @Command(value={ "KOTHSchedule Reload" })
    @Permission(value = "foxtrot.koth.admin")
    public static void kothScheduleReload(@Sender Player sender) {
        Foxtrot.getInstance().getEventHandler().loadSchedules();
        sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + "Reloaded the KOTH schedule.");
    }

}