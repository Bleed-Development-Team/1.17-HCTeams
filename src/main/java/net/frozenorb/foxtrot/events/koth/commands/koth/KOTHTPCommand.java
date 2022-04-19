package net.frozenorb.foxtrot.events.koth.commands.koth;

import me.vaperion.blade.annotation.*;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.events.Event;
import net.frozenorb.foxtrot.events.EventType;
import net.frozenorb.foxtrot.events.koth.KOTH;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHTPCommand {

    @Command(value ={ "KOTH TP", "KOTHTP", "events tp", "event tp" })
    @Permission(value = "foxtrot.koth")
    public static void kothTP(@Sender Player sender, @Name("koth") Event koth) {
        if (koth.getType() == EventType.KOTH) {
            sender.teleport(((KOTH) koth).getCapLocation().toLocation(Foxtrot.getInstance().getServer().getWorld(((KOTH) koth).getWorld())));
            sender.sendMessage(ChatColor.GRAY + "Teleported to the " + koth.getName() + " KOTH.");
        } else if (koth.getType() == EventType.DTC) {
            sender.teleport(((KOTH) koth).getCapLocation().toLocation(Foxtrot.getInstance().getServer().getWorld(((KOTH) koth).getWorld())));
            sender.sendMessage(ChatColor.GRAY + "Teleported to the " + koth.getName() + " DTC.");
        }

        sender.sendMessage(ChatColor.RED + "You can't TP to an event that doesn't have a location.");
    }

}