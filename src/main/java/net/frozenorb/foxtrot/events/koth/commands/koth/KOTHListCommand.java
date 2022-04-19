package net.frozenorb.foxtrot.events.koth.commands.koth;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.events.Event;
import net.frozenorb.foxtrot.events.EventType;
import net.frozenorb.foxtrot.events.dtc.DTC;
import net.frozenorb.foxtrot.events.koth.KOTH;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.*;

public class KOTHListCommand {
    
    @Command(value = { "KOTH List", "events list", "event list" })
    @Permission(value = "foxtrot.koth")
    public static void kothList(@Sender Player sender) {
        if (Foxtrot.getInstance().getEventHandler().getEvents().isEmpty()) {
            sender.sendMessage(RED + "There aren't any events set.");
            return;
        }
        
        for (Event event : Foxtrot.getInstance().getEventHandler().getEvents()) {
            if (event.getType() == EventType.KOTH) {
                KOTH koth = (KOTH) event;
                sender.sendMessage((koth.isHidden() ? DARK_GRAY + "[H] " : "") + (koth.isActive() ? GREEN : RED) + koth.getName() + WHITE + " - " + GRAY + TimeUtils.formatIntoMMSS(koth.getRemainingCapTime()) + DARK_GRAY + "/" + GRAY + TimeUtils.formatIntoMMSS(koth.getCapTime()) + " " + WHITE + "- " + GRAY + (koth.getCurrentCapper() == null ? "None" : koth.getCurrentCapper()));
            } else if (event.getType() == EventType.DTC) {
                DTC dtc = (DTC) event;
                sender.sendMessage((dtc.isHidden() ? DARK_GRAY + "[H] " : "") + (dtc.isActive() ? GREEN : RED) + dtc.getName() + WHITE + " - " + GRAY + "P: " + dtc.getCurrentPoints());
            }
        }
    }
    
}