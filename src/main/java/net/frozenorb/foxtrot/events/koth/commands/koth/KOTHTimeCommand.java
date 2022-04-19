package net.frozenorb.foxtrot.events.koth.commands.koth;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.events.Event;
import net.frozenorb.foxtrot.events.EventType;
import net.frozenorb.foxtrot.events.koth.KOTH;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHTimeCommand {

    @Command(value={ "KOTH Time" })
    @Permission(value = "foxtrot.koth.admin")
    public static void kothTime(@Sender Player sender, @Name("koth") Event koth, @Name("time") Float time) {
        if (time > 20F) {
            sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "This command was changed! The time parameter is now in minutes, not seconds. For example, to set a KOTH's capture time to 20 minutes 30 seconds, use /koth time 20.5");
        }

        if (koth.getType() != EventType.KOTH) {
            sender.sendMessage(ChatColor.RED + "Unable to modify cap time for a non-KOTH event.");
        } else {
            ((KOTH) koth).setCapTime((int) (time * 60F));
            sender.sendMessage(ChatColor.GRAY + "Set cap time for the " + koth.getName() + " KOTH.");
        }
    }

}