package net.frozenorb.foxtrot.events.koth.commands.koth;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.events.Event;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KOTHActivateCommand {

    @Command(value={ "KOTH Activate", "KOTH Active", "events activate" })
    @Permission(value = "foxtrot.activatekoth")
    public static void kothActivate(@Sender CommandSender sender, @Name("event") Event koth) {
        // Don't start a KOTH if another one is active.
        for (Event otherKoth : Foxtrot.getInstance().getEventHandler().getEvents()) {
            if (otherKoth.isActive()) {
                sender.sendMessage(ChatColor.RED + otherKoth.getName() + " is currently active.");
                return;
            }
        }

        if( (koth.getName().equalsIgnoreCase("citadel") || koth.getName().toLowerCase().contains("conquest")) && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "Only ops can use the activate command for weekend events.");
            return;
        }

        koth.activate();
        sender.sendMessage(ChatColor.GRAY + "Activated " + koth.getName() + ".");
    }

}
