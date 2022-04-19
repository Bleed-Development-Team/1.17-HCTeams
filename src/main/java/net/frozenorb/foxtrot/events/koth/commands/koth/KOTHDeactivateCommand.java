package net.frozenorb.foxtrot.events.koth.commands.koth;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.events.Event;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class KOTHDeactivateCommand {

    @Command(value ={ "KOTH Deactivate", "KOTH Inactive", "event deactivate" })
    @Permission(value = "foxtrot.koth.admin")
    public static void kothDectivate(@Sender CommandSender sender, @Name("koth") Event koth) {
        koth.deactivate();
        sender.sendMessage(ChatColor.GRAY + "Deactivated " + koth.getName() + " event.");
    }

}
