package net.frozenorb.foxtrot.events.koth.commands.koth;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.events.Event;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHDeleteCommand {

    @Command(value={ "KOTH Delete", "events delete", "event delete" })
    @Permission(value = "foxtrot.koth.admin")
    public static void kothDelete(@Sender Player sender, @Name("koth") Event koth) {
        Foxtrot.getInstance().getEventHandler().getEvents().remove(koth);
        Foxtrot.getInstance().getEventHandler().saveEvents();
        sender.sendMessage(ChatColor.GRAY + "Deleted event " + koth.getName() + ".");
    }

}